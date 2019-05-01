package org.arig.robot.strategy.actions.disabled.atomfactory;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.exception.RefreshPathFindingException;
import org.arig.robot.exceptions.VentouseNotAvailableException;
import org.arig.robot.model.ESide;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.Team;
import org.arig.robot.services.VentousesService;
import org.arig.robot.services.ServosService;
import org.arig.robot.strategy.AbstractAction;
import org.arig.robot.system.ITrajectoryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PrendreGoldenium extends AbstractAction {

    @Autowired
    private ITrajectoryManager mv;

    @Autowired
    private RobotStatus rs;

    @Autowired
    private ServosService servos;

    @Autowired
    private VentousesService ventouses;

    @Getter
    private boolean completed = false;

    @Override
    public String name() {
        return "Prendre le goldenium";
    }

    @Override
    public int order() {
        return 0; // TODO
    }

    @Override
    public boolean isValid() {
        return isTimeValid() &&
                rs.isAccelerateurOuvert() && !rs.isGoldeniumPrit();
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

            // aligne, prépare la ventouse et avance
            mv.gotoOrientationDeg(90);

            ventouses.waitAvailable(side);
            ventouses.preparePriseGoldenium(side);

            mv.avanceMM(150); // TODO

            // prise goldenium
            boolean ok = ventouses.priseGoldenium(side);

            // recule
            mv.reculeMM(150); // TODO

            ventouses.finishPriseGoldeniumAsync(ok, side);

            completed = true;

        } catch (NoPathFoundException | AvoidingException | VentouseNotAvailableException | RefreshPathFindingException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
            updateValidTime();
        }
    }

}
