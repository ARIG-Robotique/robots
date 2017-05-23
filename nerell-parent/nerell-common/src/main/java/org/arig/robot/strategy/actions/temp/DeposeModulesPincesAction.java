package org.arig.robot.strategy.actions.temp;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.exception.RefreshPathFindingException;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.Team;
import org.arig.robot.services.IIOService;
import org.arig.robot.services.ServosService;
import org.arig.robot.strategy.AbstractAction;
import org.arig.robot.system.ITrajectoryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DeposeModulesPincesAction extends AbstractAction {

    @Autowired
    private ITrajectoryManager mv;

    @Autowired
    private RobotStatus rs;

    @Autowired
    private IIOService ioService;

    @Autowired
    private ServosService servosService;

    @Getter
    private boolean completed = false;

    @Override
    public String name() {
        return "Dépose des modules dans la pince dans la zone de départ";
    }

    @Override
    public int order() {
        return 20;
    }

    @Override
    public boolean isValid() {
        return rs.getModuleLunaireCentre() != null && rs.getModuleLunaireDroite() != null ||
                rs.getElapsedTime() > IConstantesNerellConfig.matchTimeMs / 2 && (rs.getModuleLunaireCentre() != null || rs.getModuleLunaireDroite() != null);
    }

    @Override
    public void execute() {
        try {
            rs.enableAvoidance();
            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);

            if (Team.JAUNE == rs.getTeam()) {
                mv.pathTo(960, 260);

            } else {
                mv.pathTo(3000 - 960, 260);
            }

            mv.gotoOrientationDeg(-90);

            servosService.brasAttentePriseRobot();
            servosService.pinceDroiteOuvert();
            servosService.waitBras();

            mv.reculeMM(300);

            if (rs.getModuleLunaireDroite() != null && !ioService.presencePinceDroite()) {
                rs.setModuleLunaireDroite(null);
            }

            if (rs.getModuleLunaireCentre() != null && !ioService.presencePinceCentre()) {
                rs.setModuleLunaireCentre(null);
            }

            completed = !ioService.presencePinceDroite() && !ioService.presencePinceCentre();

        } catch (NoPathFoundException | AvoidingException | RefreshPathFindingException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
        }
    }
}
