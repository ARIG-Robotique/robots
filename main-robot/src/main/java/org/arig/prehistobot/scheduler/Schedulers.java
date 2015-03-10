package org.arig.prehistobot.scheduler;

import org.arig.prehistobot.constants.IConstantesRobot;
import org.arig.prehistobot.model.RobotStatus;
import org.arig.robot.system.MouvementManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by gdepuille on 23/12/14.
 */
@Component
@Profile("raspi")
public class Schedulers {

    @Autowired
    private RobotStatus rs;

    @Autowired
    private MouvementManager mouvementManager;

    @Scheduled(fixedRate = (long) IConstantesRobot.asservTimeMs)
    public void robotManagerTask() {
        if (rs.isAsservEnabled()) {
            mouvementManager.process();
        }
    }
}
