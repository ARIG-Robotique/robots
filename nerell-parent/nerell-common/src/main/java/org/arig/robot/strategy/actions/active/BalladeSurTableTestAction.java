package org.arig.robot.strategy.actions.active;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.exception.ObstacleFoundException;
import org.arig.robot.model.Point;
import org.arig.robot.model.Position;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.Team;
import org.arig.robot.services.ServosService;
import org.arig.robot.strategy.IAction;
import org.arig.robot.system.MouvementManager;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @author gdepuille on 06/11/16.
 */
@Slf4j
@Component
public class BalladeSurTableTestAction implements IAction {

    @Autowired
    private MouvementManager mv;

    @Autowired
    private RobotStatus rs;

    @Getter
    private boolean completed = false; // Jamais terminé quoi qu'il se passe

    @Override
    public String name() {
        return "Ballade sur table de test";
    }

    @Override
    public int order() {
        return 1;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void execute() {
        try {
            rs.enableAscenseur();
            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);
            /*mv.pathTo(900, 1550);
            mv.pathTo(300, 1400);
            mv.pathTo(400, 1200);
            mv.pathTo(680, 1300);
            mv.pathTo(780, 1200);
            mv.pathTo(900, 260);
            mv.pathTo(300, 260);
            mv.pathTo(300, 300);
            mv.pathTo(590, 1550);
            mv.pathTo(590, 260);*/

            mv.pathTo(590, 1550);
            mv.pathTo(590, 300);

        } catch (NoPathFoundException | AvoidingException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
        }
    }
}
