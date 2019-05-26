package org.arig.robot.strategy.actions.disabled.atomfactory;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.exception.RefreshPathFindingException;
import org.arig.robot.exceptions.VentouseNotAvailableException;
import org.arig.robot.model.ESide;
import org.arig.robot.model.EStrategy;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.Team;
import org.arig.robot.services.VentousesService;
import org.arig.robot.strategy.AbstractAction;
import org.arig.robot.system.ITrajectoryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Component
@Slf4j
public class PrendreGoldeniumAdverse extends AbstractAction {

    @Autowired
    private RobotStatus rs;

    @Autowired
    private ITrajectoryManager mv;

    @Autowired
    private VentousesService ventouses;

    @Getter
    private boolean completed = false;

    @Override
    public String name() {
        return "Prise goldenium de l'adversaire";
    }

    @Override
    public int order() {
        return 20;
    }

    @Override
    public boolean isValid() {
        return rs.strategyActive(EStrategy.PRIS_GOLDENIUM_ADVERSE) && rs.getRemainingTime() < 10000;
    }

    @Override
    public void execute() {

        ESide side = rs.getTeam() == Team.VIOLET ? ESide.GAUCHE : ESide.DROITE;

        try {
            rs.enableAvoidance();
            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);

            int yAvantAvance = 1725;

            // va au point le plus proche
            if (rs.getTeam() == Team.VIOLET) {
                mv.pathTo(2500 - 235 - 40 + 50, yAvantAvance);
            } else {
                // 235 = distance bord accelerateur/bord support gold, 40 = moitié support gold
                mv.pathTo(500 + 235 + 40 - 50, yAvantAvance);
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
        } catch (NoPathFoundException | AvoidingException | RefreshPathFindingException | VentouseNotAvailableException | ExecutionException | InterruptedException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
            updateValidTime();
        } finally {
            completed = true;
        }

    }

    @Override
    public String getUUID() {
        return null;
    }
}
