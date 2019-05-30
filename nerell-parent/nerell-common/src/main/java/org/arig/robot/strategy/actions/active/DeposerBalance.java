package org.arig.robot.strategy.actions.active;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.exception.RefreshPathFindingException;
import org.arig.robot.exceptions.CarouselNotAvailableException;
import org.arig.robot.exceptions.VentouseNotAvailableException;
import org.arig.robot.model.ESide;
import org.arig.robot.model.Position;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.Team;
import org.arig.robot.model.enums.CouleurPalet;
import org.arig.robot.services.CarouselService;
import org.arig.robot.services.IRobotSide;
import org.arig.robot.services.IVentousesService;
import org.arig.robot.services.ServosService;
import org.arig.robot.strategy.AbstractAction;
import org.arig.robot.system.ICarouselManager;
import org.arig.robot.system.ITrajectoryManager;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.arig.robot.utils.TableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class DeposerBalance extends AbstractAction {

    @Autowired
    private ITrajectoryManager mv;

    @Autowired
    private RobotStatus rs;

    @Autowired
    private IVentousesService ventouses;

    @Autowired
    private ICarouselManager carousel;

    @Autowired
    @Qualifier("sideServices")
    private Map<ESide, IRobotSide> sideServices;

    @Autowired
    private CarouselService carouselService;

    @Autowired
    private TableUtils tableUtils;

//    @Autowired
//    private ConvertionRobotUnit conv;
//
//    @Autowired
//    @Qualifier("currentPosition")
//    private Position currentPosition; // Attention ce sont des pulses

    @Getter
    private boolean completed = false;

    @Override
    public String name() {
        return "Déposer des palets dans la balance";
    }

    @Override
    public int order() {
        // 24 pour le gold
        int points = (ventouses.getCouleur(rs.mainSide()) == CouleurPalet.GOLD ? 24 : 0) +
                // 12 pour les bleus
                (int) Math.min(IConstantesNerellConfig.nbPaletsBalanceMax - rs.getPaletsInBalance().size(), carousel.count(CouleurPalet.BLEU)) * 12 +
                // 8 pour les verts
                (int) Math.min(IConstantesNerellConfig.nbPaletsBalanceMax - rs.getPaletsInBalance().size(), carousel.count(CouleurPalet.VERT)) * 8;
        return points;
    }

    @Override
    public boolean isValid() {
        return isTimeValid() && canDepose() &&
                (
                        rs.getRemainingTime() < 70000 ||
                                carousel.count(CouleurPalet.BLEU) + carousel.count(CouleurPalet.VERT) >= 3
                );
    }

    private boolean canDepose() {
        return rs.getPaletsInBalance().size() < IConstantesNerellConfig.nbPaletsBalanceMax &&
                (
                        ventouses.getCouleur(rs.mainSide()) == CouleurPalet.GOLD ||
                                carousel.has(CouleurPalet.BLEU) ||
                                carousel.has(CouleurPalet.VERT)
                );
    }

    @Override
    public void execute() {
        ESide side = rs.mainSide();

        try {
            carouselService.setHint(side.getPositionVentouse(), carousel.has(CouleurPalet.BLEU) ? CouleurPalet.BLEU : CouleurPalet.VERT);
            rs.disableMagasin();

            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);

            rs.enableAvoidance();

            int yAvantAvance = 795;

            // 150 = moitié du séparateur +  moitié de la balance + marge
            // va au point le plus proche
            if (rs.getTeam() == Team.VIOLET) {
                tableUtils.addDynamicDeadZone(new java.awt.Rectangle.Double(1000, 420, 500, 330));
                mv.pathTo(1500 + 150 + IConstantesNerellConfig.dstAtomeCentre, yAvantAvance);
            } else {
                tableUtils.addDynamicDeadZone(new java.awt.Rectangle.Double(1500, 420, 500, 330));
                mv.pathTo(1500 - 150 - IConstantesNerellConfig.dstAtomeCentre, yAvantAvance);
            }

            rs.disableAvoidance();

            if (ventouses.getCouleur(side) != CouleurPalet.GOLD) {
                ventouses.waitAvailable(ESide.DROITE);
                ventouses.waitAvailable(ESide.GAUCHE);
            } else {
                ventouses.waitAvailable(side == ESide.GAUCHE ? ESide.DROITE : ESide.GAUCHE);
            }

            rs.disableVentouses();
            rs.disableCarousel();

            ventouses.prepareDeposeBalance(side);

            mv.setVitesse(IConstantesNerellConfig.vitesseMouvement, IConstantesNerellConfig.vitesseOrientation);

            if (rs.getTeam() == Team.VIOLET) {
                mv.gotoPointMM(1500 + 150 + IConstantesNerellConfig.dstAtomeCentre, 588, true);
            } else {
                mv.gotoPointMM(1500 - 150 - IConstantesNerellConfig.dstAtomeCentre, 588, true);
            }

            mv.gotoOrientationDeg(-90);

            // Opération vomito
            // TODO Stocker les rouge dans le magasin et compter les points correctement
            sideServices.get(ESide.DROITE).porteBarilletOuvert(false);
            sideServices.get(ESide.GAUCHE).porteBarilletOuvert(true);
            carousel.tourneIndex(8);
            carousel.tourneIndex(-8);

            /*while (canDepose()) {
                CouleurPalet couleur = ventouses.getCouleur(side) == CouleurPalet.GOLD ?
                        CouleurPalet.GOLD :
                        carousel.has(CouleurPalet.BLEU) ? CouleurPalet.BLEU : CouleurPalet.VERT;

                if (!ventouses.deposeBalance(couleur, side)) {
                    break;
                }
            }*/

            mv.reculeMM(150);
            sideServices.get(ESide.DROITE).porteBarilletFerme(false);
            sideServices.get(ESide.GAUCHE).porteBarilletFerme(true);

            completed = rs.getPaletsInBalance().size() >= IConstantesNerellConfig.nbPaletsBalanceMax;

            // FIXME
            completed = true;

        } catch (NoPathFoundException | AvoidingException | RefreshPathFindingException | VentouseNotAvailableException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
            updateValidTime();

            try {
                mv.reculeMM(100);
            } catch (RefreshPathFindingException | AvoidingException ex) {
                ex.printStackTrace();
            }

        }

        tableUtils.clearDynamicDeadZones();
        ventouses.releaseSide(ESide.GAUCHE);
        ventouses.releaseSide(ESide.DROITE);
        rs.enableMagasin();
        rs.enableVentouses();
        rs.enableCarousel();
        ventouses.finishDepose(side);
    }

}
