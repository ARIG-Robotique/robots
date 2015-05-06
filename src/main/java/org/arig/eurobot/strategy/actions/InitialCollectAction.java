package org.arig.eurobot.strategy.actions;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.exception.ObstacleFoundException;
import org.arig.robot.strategy.IAction;
import org.arig.robot.system.MouvementManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by gdepuille on 06/05/15.
 */
@Slf4j
@Component
public class InitialCollectAction implements IAction {

    @Autowired
    private MouvementManager mv;

    @Getter
    private final boolean valid = true;

    @Getter
    private boolean completed = false;

    private int step = 0;

    @Override
    public String name() {
        return "Collect initiale des objets";
    }

    @Override
    public int order() {
        return 1000;
    }

    @Override
    public void execute() {
        log.info("Execution de l'action de collecte initiale");
        try {
            if (step == 0) {
                mv.gotoPointMM(365, 210);
                step++;
            }
            if (step == 1) {
                mv.pathTo(900, 1400);
                step++;
            }
            if (step == 2) {
                mv.pathTo(250, 1200);
                step++;
            }
            if (step == 3) {
                mv.pathTo(900, 500);
                step++;
            }
            if (step == 4) {
                mv.pathTo(250, 500);
                completed = true;
            }
        } catch (NoPathFoundException | ObstacleFoundException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
        }
    }
}
