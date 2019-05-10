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
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.Team;
import org.arig.robot.model.enums.CouleurPalet;
import org.arig.robot.services.VentousesService;
import org.arig.robot.strategy.AbstractAction;
import org.arig.robot.system.ICarouselManager;
import org.arig.robot.system.ITrajectoryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DeposerBalanceSansCarousel extends AbstractAction {

    @Autowired
    private ITrajectoryManager mv;

    @Autowired
    private RobotStatus rs;

    @Autowired
    private VentousesService ventouses;

    @Autowired
    private ICarouselManager carousel;

    @Getter
    private boolean completed = false;

    @Override
    public String name() {
        return "Déposer des palets dans la balance";
    }

    @Override
    public int order() {
        ESide side = rs.getTeam() == Team.VIOLET ? ESide.DROITE : ESide.GAUCHE;
        if (rs.isGoldeniumPrit() && ventouses.getCouleur(side) == CouleurPalet.GOLD) {
            return Integer.MAX_VALUE - 3;
        } else {
            int points = Math.min(IConstantesNerellConfig.nbPaletsBalanceMax - rs.getPaletsInBalance().size(), ventouses.getCouleur(side) == CouleurPalet.BLEU ? 1 : 0) * 12 +
                    // 8 pour les verts
                    Math.min(IConstantesNerellConfig.nbPaletsBalanceMax - rs.getPaletsInBalance().size(), ventouses.getCouleur(side) == CouleurPalet.VERT ? 1 : 0) * 8;

            return points;
        }
    }

    @Override
    public boolean isValid() {
        return isTimeValid() && canDepose();
    }

    private boolean canDepose() {
        ESide side = rs.getTeam() == Team.VIOLET ? ESide.DROITE : ESide.GAUCHE;
        return rs.getPaletsInBalance().size() < IConstantesNerellConfig.nbPaletsBalanceMax &&
                (
                        ventouses.getCouleur(side) == CouleurPalet.GOLD ||
                                ventouses.getCouleur(side) == CouleurPalet.BLEU ||
                                ventouses.getCouleur(side) == CouleurPalet.VERT
                );
    }

    @Override
    public void execute() {
        ESide side = rs.getTeam() == Team.VIOLET ? ESide.DROITE : ESide.GAUCHE;
        mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);
        try {
            rs.enableAvoidance();

            int yAvantAvance = 795;

            // va au point le plus proche
            if (rs.getTeam() == Team.VIOLET) {
                // 20 = moitié du séparateur, 110 = moitié de la balance, 50 = marge de sécu
                mv.pathTo(1500 + 130 + 50 + IConstantesNerellConfig.dstAtomeCentre, yAvantAvance);
            } else {
                mv.pathTo(1500 - 130 - 50 - IConstantesNerellConfig.dstAtomeCentre, yAvantAvance);
            }

            rs.disableAvoidance();

            mv.gotoOrientationDeg(-90);

            CouleurPalet couleur = ventouses.getCouleur(side) == CouleurPalet.GOLD ? CouleurPalet.GOLD : CouleurPalet.ANY;
            if (!ventouses.deposeBalance1(couleur, side)) {
                throw new VentouseNotAvailableException();
            }

            // 400 = longueur de la balance
            double yOffset = -400 + yAvantAvance - IConstantesNerellConfig.dstVentouseFacade;

            mv.avanceMM(yOffset);

            ventouses.deposeBalance2(side);

            mv.reculeMM(yOffset);

            completed = rs.getPaletsInBalance().size() >= IConstantesNerellConfig.nbPaletsBalanceMax;

        } catch (NoPathFoundException | AvoidingException | RefreshPathFindingException | CarouselNotAvailableException | VentouseNotAvailableException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
            updateValidTime();

        } finally {
            ventouses.finishDeposeAsync(side);
        }
    }

}
