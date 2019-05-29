package org.arig.robot.strategy.actions.disabled.atomfactory;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.exception.RefreshPathFindingException;
import org.arig.robot.exceptions.CarouselNotAvailableException;
import org.arig.robot.exceptions.VentouseNotAvailableException;
import org.arig.robot.model.ESide;
import org.arig.robot.model.EStrategy;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.Team;
import org.arig.robot.model.enums.CouleurPalet;
import org.arig.robot.services.CarouselService;
import org.arig.robot.services.IVentousesService;
import org.arig.robot.strategy.AbstractAction;
import org.arig.robot.system.ICarouselManager;
import org.arig.robot.system.ITrajectoryManager;
import org.arig.robot.utils.TableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;

@Slf4j
@Component
public class DeposeAccelerateur extends AbstractAction {

    @Autowired
    private ITrajectoryManager mv;

    @Autowired
    private RobotStatus rs;

    @Autowired
    private IVentousesService ventouses;

    @Autowired
    private ICarouselManager carousel;

    @Autowired
    private CarouselService carouselService;

    @Autowired
    private TableUtils tableUtils;

    @Getter
    private boolean completed = false;

    @Override
    public String name() {
        return "Active l'accelerateur et dépose";
    }

    @Override
    public int order() {
        // 10 pour ouvrir l'accelerateur
        int points = (!rs.isAccelerateurOuvert() ? 10 + 20 : 0) +
                // 10 par palet
                (int) Math.min(IConstantesNerellConfig.nbPaletsAccelerateurMax - rs.getPaletsInAccelerateur().size(), rs.getRemainingTime() < 30000 ? carousel.count(CouleurPalet.ANY) : carousel.count(CouleurPalet.ROUGE)) * 10;
        return points;
    }

    @Override
    public boolean isValid() {
        return isTimeValid() &&
                (
                        rs.getRemainingTime() < 70000 ||
                                carousel.count(CouleurPalet.ROUGE) >= 3
                ) &&
                (
                        !rs.isAccelerateurOuvert() ||
                                canDepose() ||
                                rs.strategyActive(EStrategy.PRISE_BLEU_ACCELERATEUR) && !rs.isAccelerateurPrit() && carousel.has(null)
                );
    }

    public boolean canDepose() {
        return rs.getPaletsInAccelerateur().size() < IConstantesNerellConfig.nbPaletsAccelerateurMax &&
                (
                        carousel.has(CouleurPalet.ROUGE) ||
                                carousel.has(CouleurPalet.ANY) && rs.getRemainingTime() < 30000
                );
    }

    @Override
    public void execute() {
        ESide side = rs.mainSide();
        ESide sideDepose = side == ESide.GAUCHE ? ESide.DROITE : ESide.GAUCHE;

        try {
            carouselService.setHint(sideDepose.getPositionVentouse(), carousel.has(CouleurPalet.ROUGE) ? CouleurPalet.ROUGE : CouleurPalet.ANY);
            rs.disableMagasin();

            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);

            rs.enableAvoidance();

            int yAvantAvance = 1700;

            // va au point le plus proche
            if (rs.getTeam() == Team.VIOLET) {
                tableUtils.addDynamicDeadZone(new Rectangle.Double(1700, 1600, 300, 400));
                mv.pathTo(2300, 1400);
                mv.pathTo(1500, 1000);
                mv.pathTo(1500 - 210 - IConstantesNerellConfig.dstAtomeCentre + 15, yAvantAvance);
            } else {
                tableUtils.addDynamicDeadZone(new Rectangle.Double(1000, 1600, 300, 400));
                mv.pathTo(700, 1400);
                mv.pathTo(1500, 1000);
                mv.pathTo(1500 + 210 + IConstantesNerellConfig.dstAtomeCentre - 15, yAvantAvance);
            }

            rs.disableAvoidance();

            ventouses.waitAvailable(ESide.DROITE);
            ventouses.waitAvailable(ESide.GAUCHE);

            rs.disableCarousel();
            rs.disableVentouses();

            if (rs.strategyActive(EStrategy.PRISE_BLEU_ACCELERATEUR)) {
                if (!ventouses.preparePriseAccelerateur(side, sideDepose)) {
                    throw new CarouselNotAvailableException();
                }
            } else {
                ventouses.prepareDeposeAccelerateur(side, sideDepose);
            }

            mv.setVitesse(IConstantesNerellConfig.vitesseMouvement, IConstantesNerellConfig.vitesseOrientation);

            if (rs.getTeam() == Team.VIOLET) {
                mv.gotoPointMM(1240, 1800 + 3);
            } else {
                mv.gotoPointMM(3000 - 1240, 1800 + 3);
            }

            mv.gotoOrientationDeg(90);

            // prend ou pousse le bleu
            if (rs.strategyActive(EStrategy.PRISE_BLEU_ACCELERATEUR) && !rs.isAccelerateurPrit()) {
                if (ventouses.priseAccelerateur(side)) {
                    ventouses.stockageCarouselMaisResteEnHaut(side);
                    rs.setAccelerateurPrit(true);

                } else if (!rs.isAccelerateurOuvert()) {
                    ventouses.pousseAccelerateur(side);
                    rs.setAccelerateurOuvert(true);
                    rs.getPaletsInAccelerateur().add(CouleurPalet.BLEU);
                }
            } else if (!rs.isAccelerateurOuvert() && !rs.isAccelerateurPrit()) {
                ventouses.pousseAccelerateur(side);
                rs.setAccelerateurOuvert(true);
                rs.getPaletsInAccelerateur().add(CouleurPalet.BLEU);
            }

            // dépose
            while (canDepose()) {
                CouleurPalet couleur = carousel.has(CouleurPalet.ROUGE) ? CouleurPalet.ROUGE : CouleurPalet.ANY;

                if (!ventouses.deposeAccelerateur(couleur, sideDepose)) {
                    break;
                }

                // cas ou a prit le bleu
                if (!rs.isAccelerateurOuvert()) {
                    rs.setAccelerateurOuvert(true);
                }
            }

            completed = rs.getPaletsInAccelerateur().size() >= IConstantesNerellConfig.nbPaletsAccelerateurMax;

        } catch (NoPathFoundException | AvoidingException | RefreshPathFindingException | VentouseNotAvailableException | CarouselNotAvailableException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
            updateValidTime();
        }

        tableUtils.clearDynamicDeadZones();

        try {
            mv.reculeMM(50);
            ventouses.finishDeposeAccelerateur(side, sideDepose);
            ventouses.releaseSide(ESide.GAUCHE);
            ventouses.releaseSide(ESide.DROITE);
            rs.enableMagasin();
            rs.enableCarousel();
            rs.enableVentouses();

        } catch (RefreshPathFindingException | AvoidingException e) {
            log.error("Erreur d'éxécution de la finalisation de l'action : {}", e.toString());
        }
    }

}
