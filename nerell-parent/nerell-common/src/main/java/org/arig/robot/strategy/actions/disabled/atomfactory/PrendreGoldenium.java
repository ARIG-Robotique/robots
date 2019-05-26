package org.arig.robot.strategy.actions.disabled.atomfactory;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.exception.RefreshPathFindingException;
import org.arig.robot.exceptions.VentouseNotAvailableException;
import org.arig.robot.model.ESide;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.Team;
import org.arig.robot.services.VentousesService;
import org.arig.robot.strategy.AbstractAction;
import org.arig.robot.system.ITrajectoryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Slf4j
@Component
public class PrendreGoldenium extends AbstractAction {

    @Autowired
    private ITrajectoryManager mv;

    @Autowired
    private RobotStatus rs;

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
        return 20 + 24;
    }

    @Override
    public boolean isValid() {
        return isTimeValid() &&
                rs.isAccelerateurOuvert() && !rs.isGoldeniumPrit();
    }

    @Override
    public void execute() {
        ESide side = rs.mainSide();

        try {
            rs.enableAvoidance();

            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);

            int yAvantAvance = 1725;

            // va au point le plus proche
            if (rs.getTeam() == Team.VIOLET) {
                // 235 = distance bord accelerateur/bord support gold, 40 = moitié support gold
                mv.pathTo(500 + 235 + 40 - 50, yAvantAvance);
            } else {
                mv.pathTo(2500 - 235 - 40 + 50, yAvantAvance);
            }

            rs.disableAvoidance();

            // aligne, prépare la ventouse et avance
            mv.gotoOrientationDeg(90);

            ventouses.waitAvailable(side);
            ventouses.preparePriseGoldenium(side).get();

            mv.avanceMM(2000 - 50 - yAvantAvance - IConstantesNerellConfig.dstVentouseFacade);

            // prise goldenium
            boolean ok = ventouses.priseGoldenium(side).get();

            ventouses.finishPriseGoldenium(ok, side);

            completed = true;

        } catch (NoPathFoundException | AvoidingException | VentouseNotAvailableException | RefreshPathFindingException | InterruptedException | ExecutionException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
            updateValidTime();
        }
    }

}
