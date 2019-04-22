package org.arig.robot.strategy.actions.disabled.atomfactory;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.exception.RefreshPathFindingException;
import org.arig.robot.exceptions.CarouselNotAvailableException;
import org.arig.robot.exceptions.PinceNotAvailableException;
import org.arig.robot.model.ESide;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.Team;
import org.arig.robot.model.enums.CouleurPalet;
import org.arig.robot.services.PincesService;
import org.arig.robot.strategy.AbstractAction;
import org.arig.robot.system.ITrajectoryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ActiverAccelerateur extends AbstractAction {

    @Autowired
    private ITrajectoryManager mv;

    @Autowired
    private RobotStatus rs;

    @Autowired
    private PincesService pinces;

    @Getter
    private boolean completed = false;

    @Override
    public String name() {
        return "Active l'accelerateur et dépose si possible";
    }

    @Override
    public int order() {
        return 0; // TODO
    }

    @Override
    public boolean isValid() {
        return !rs.isAccelerateurOuvert();
    }

    @Override
    public void execute() {
        ESide side = rs.getTeam() == Team.VIOLET ? ESide.DROITE : ESide.GAUCHE;

        try {
            rs.enableAvoidance();

            // va au point le plus proche
            // TODO
            if (rs.getTeam() == Team.VIOLET) {
                mv.pathTo(1200, 1800);
            } else {
                mv.pathTo(1700, 1800);
            }

            rs.disableAvoidance();

            pinces.waitAvailable(side);

            pinces.prepareDeposeAccelerateur(side);

            // oriente et avance à fond
            mv.gotoOrientationDeg(90);
            mv.avanceMM(150); // TODO

            // pousse le bleu
            pinces.pousseAccelerateur(side);
            rs.setAccelerateurOuvert(true);

            // depose du rouge
            try {
                while (pinces.deposeAccelerateur(CouleurPalet.ROUGE, side)) {
                    // on fait confiance au service !
                }
            } catch (CarouselNotAvailableException e) {
                // si erreur carousel on ignore l'erreur, les palets seront déposés avec une autre action
            }

            mv.reculeMM(150); // TODO

            completed = true;

        } catch (NoPathFoundException | AvoidingException | RefreshPathFindingException | PinceNotAvailableException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
            updateValidTime();

        } finally {
            pinces.finishDeposeAccelerateur(side);
        }
    }

}
