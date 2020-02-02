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
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.Team;
import org.arig.robot.model.enums.CouleurPalet;
import org.arig.robot.services.CarouselService;
import org.arig.robot.services.IVentousesService;
import org.arig.robot.services.MagasinService;
import org.arig.robot.strategy.AbstractAction;
import org.arig.robot.system.ICarouselManager;
import org.arig.robot.system.ITrajectoryManager;
import org.arig.robot.utils.TableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DeposerGoldeniumBalance extends AbstractAction {

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

    @Autowired
    private MagasinService magasin;

    @Getter
    private boolean completed = false;

    private boolean even = false;

    @Override
    public String name() {
        return "Déposer le goldenium dans la balance";
    }

    @Override
    public int order() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isValid() {
        return isTimeValid() &&  ventouses.getCouleur(rs.mainSide()) == CouleurPalet.GOLD;
    }

    @Override
    public void execute() {
        ESide side = rs.mainSide();
        ESide otherSide = side == ESide.GAUCHE ? ESide.DROITE : ESide.GAUCHE;

        try {
            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);

            rs.enableAvoidance();

            int yAvantAvance = 795;

            even = !even;

            // 150 = moitié du séparateur +  moitié de la balance + marge
            // va au point le plus proche
            if (rs.getTeam() == Team.VIOLET) {
                tableUtils.addDynamicDeadZone(new java.awt.Rectangle.Double(1000, 420, 500, 330));
                if (even) {
                    mv.pathTo(2000, 1200);
                } else {
                    mv.pathTo(1800, 1000);
                }
                mv.pathTo(1500 + 160 + IConstantesNerellConfig.dstAtomeCentre, yAvantAvance);
            } else {
                tableUtils.addDynamicDeadZone(new java.awt.Rectangle.Double(1500, 420, 500, 330));
                if (even) {
                    mv.pathTo(1000, 1200);
                } else {
                    mv.pathTo(1200, 1000);
                }
                mv.pathTo(1500 - 160 - IConstantesNerellConfig.dstAtomeCentre, yAvantAvance);
            }

            rs.disableAvoidance();
            rs.disableMagasin();

            ventouses.waitAvailable(side == ESide.GAUCHE ? ESide.DROITE : ESide.GAUCHE, 15000);

            rs.disableVentouses();

            ventouses.prepareDeposeBalance(side);

            mv.setVitesse(IConstantesNerellConfig.vitesseMouvement, IConstantesNerellConfig.vitesseOrientation);

            mv.gotoOrientationDeg(-90);

            if (rs.getTeam() == Team.VIOLET) {
                mv.gotoPointMM(1500 + 160 + IConstantesNerellConfig.dstAtomeCentre, 588, false);
            } else {
                mv.gotoPointMM(1500 - 160 - IConstantesNerellConfig.dstAtomeCentre, 588, false);
            }

            mv.gotoOrientationDeg(-90);

            ventouses.deposeBalance(CouleurPalet.GOLD, side);

            magasin.digerer(CouleurPalet.ROUGE);

            if ((rs.getPaletsInBalance().size() + carousel.count(CouleurPalet.ANY))
                    <= IConstantesNerellConfig.nbPaletsBalanceMax) {
                ventouses.prepareVomiBalance(side);
                ventouses.vomiBalance(side);
            }

            mv.reculeMM(150);

            completed = true;

        } catch (NoPathFoundException | AvoidingException | RefreshPathFindingException | VentouseNotAvailableException | CarouselNotAvailableException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
            updateValidTime();

            try {
                mv.reculeMM(150);
            } catch (RefreshPathFindingException | AvoidingException ex) {
                ex.printStackTrace();
            }

        }

        tableUtils.clearDynamicDeadZones();
        ventouses.releaseSide(otherSide);
        rs.enableVentouses();
        rs.enableMagasin();
        if (ventouses.getCouleur(side) != CouleurPalet.GOLD) {
            ventouses.finishDepose(side);
        }
    }

}