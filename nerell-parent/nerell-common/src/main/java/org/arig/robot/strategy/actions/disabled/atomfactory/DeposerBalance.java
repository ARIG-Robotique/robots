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
import org.arig.robot.services.VentousesService;
import org.arig.robot.strategy.AbstractAction;
import org.arig.robot.system.ICarouselManager;
import org.arig.robot.system.ITrajectoryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DeposerBalance extends AbstractAction {

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
        return 0; // TODO
    }

    @Override
    public boolean isValid() {
        return isTimeValid() && canDepose();
    }

    private boolean canDepose() {
        return rs.getPaletsInBalance().size() < IConstantesNerellConfig.nbPaletsBalanceMax &&
                (
                        carousel.has(CouleurPalet.BLEU) ||
                                carousel.has(CouleurPalet.VERT) && rs.getRemainingTime() < 30
                );
    }

    @Override
    public void execute() {
        ESide side = rs.getTeam() == Team.VIOLET ? ESide.DROITE : ESide.GAUCHE;

        try {
            rs.enableAvoidance();

            // va au point le plus proche
            // TODO
            if (rs.getTeam() == Team.VIOLET) {
                mv.pathTo(1700, 600);
            } else {
                mv.pathTo(1300, 600);
            }

            rs.disableAvoidance();

            ventouses.waitAvailable(side);

            while (canDepose()) {
                CouleurPalet couleur = carousel.has(CouleurPalet.BLEU) ? CouleurPalet.BLEU : CouleurPalet.VERT;

                if (!ventouses.deposeBalance1(couleur, side)) {
                    throw new VentouseNotAvailableException();
                }

                mv.avanceMM(150); // TODO

                ventouses.deposeBalance2(side);

                mv.reculeMM(150); // TODO
            }

            completed = rs.getPaletsInBalance().size() >= IConstantesNerellConfig.nbPaletsBalanceMax;

        } catch (NoPathFoundException | AvoidingException | RefreshPathFindingException | CarouselNotAvailableException | VentouseNotAvailableException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
            updateValidTime();

        } finally {
            ventouses.finishDeposeAsync(side);
        }
    }

}
