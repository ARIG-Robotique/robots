package org.arig.robot.strategy.actions.disabled.atomfactory;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.exception.RefreshPathFindingException;
import org.arig.robot.exceptions.VentouseNotAvailableException;
import org.arig.robot.model.ESide;
import org.arig.robot.model.Position;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.Team;
import org.arig.robot.services.ServosService;
import org.arig.robot.services.VentousesService;
import org.arig.robot.strategy.AbstractAction;
import org.arig.robot.system.ITrajectoryManager;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

    @Autowired
    @Qualifier("currentPosition")
    private Position position;

    @Autowired
    private ConvertionRobotUnit conv;

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
            if (rs.getTeam() == Team.VIOLET) {
                mv.pathTo(500 + 235 + 40 - 50, 1725);
            } else {
                mv.pathTo(2500 - 235 - 40 + 50, 1725);
            }

            rs.disableAvoidance();

            // aligne, prépare la ventouse et avance
            mv.gotoOrientationDeg(90);

            ventouses.waitAvailable(side);
            ventouses.preparePriseGoldenium(side);
            mv.gotoPointMM(conv.pulseToMm(position.getPt().getX()), 2000 - 55 - IConstantesNerellConfig.dstVentouseFacade);

            // prise goldenium
            boolean ok = ventouses.priseGoldenium(side);

            // recule
            mv.reculeMM(50);

            ventouses.finishPriseGoldeniumAsync(ok, side);

            completed = true;

        } catch (NoPathFoundException | AvoidingException | VentouseNotAvailableException | RefreshPathFindingException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
            updateValidTime();
        }
    }

}
