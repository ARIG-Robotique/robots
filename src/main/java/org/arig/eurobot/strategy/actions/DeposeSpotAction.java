package org.arig.eurobot.strategy.actions;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.eurobot.constants.IConstantesServos;
import org.arig.eurobot.model.RobotStatus;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.exception.ObstacleFoundException;
import org.arig.robot.strategy.IAction;
import org.arig.robot.system.MouvementManager;
import org.arig.robot.system.servos.SD21Servos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by gdepuille on 06/05/15.
 */
@Slf4j
@Component
public class DeposeSpotAction implements IAction {

    @Autowired
    private MouvementManager mv;

    @Autowired
    private SD21Servos servos;

    @Autowired
    private RobotStatus rs;

    @Getter
    private final boolean completed = false;

    @Override
    public String name() {
        return "Dépose spot action";
    }

    @Override
    public int order() {
        return 1000;
    }

    @Override
    public boolean isValid() {
        return rs.getNbPied() > 3;
    }

    @Override
    public void execute() {
        log.info("Execution de l'action de dépose d'un spot");
        try {
            mv.pathTo(700, 210);
            mv.gotoOrientationDeg(0);
            mv.avanceMM(200);
            servos.setPositionAndWait(IConstantesServos.ASCENSEUR, IConstantesServos.ASCENSEUR_BAS);
            servos.setPosition(IConstantesServos.PINCE, IConstantesServos.PINCE_OUVERTE);
            servos.setPosition(IConstantesServos.GUIDE, IConstantesServos.GUIDE_OUVERT);
            mv.reculeMM(200);
            rs.resetNbPied();
        } catch (NoPathFoundException | ObstacleFoundException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
        }
    }
}
