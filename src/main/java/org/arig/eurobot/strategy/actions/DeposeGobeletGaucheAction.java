package org.arig.eurobot.strategy.actions;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.eurobot.constants.IConstantesServos;
import org.arig.eurobot.model.RobotStatus;
import org.arig.eurobot.services.ServosService;
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
public class DeposeGobeletGaucheAction implements IAction {

    @Autowired
    private MouvementManager mv;

    @Autowired
    private ServosService servosService;

    @Autowired
    private RobotStatus rs;

    @Getter
    private final boolean completed = false;

    @Override
    public String name() {
        return "Dépose du gobelet gauche";
    }

    @Override
    public int order() {
        return 500;
    }

    @Override
    public boolean isValid() {
        return rs.isProduitGauche();
    }

    @Override
    public void execute() {
        log.info("Execution de l'action de dépose du gobelet gauche");
        try {
            mv.pathTo(210, 210);
            mv.gotoOrientationDeg(180);
            mv.avanceMM(200);
            servosService.deposeGobeletGauche();
            mv.reculeMM(200);
            rs.setProduitGauche(false);
        } catch (NoPathFoundException | ObstacleFoundException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
        }
    }
}
