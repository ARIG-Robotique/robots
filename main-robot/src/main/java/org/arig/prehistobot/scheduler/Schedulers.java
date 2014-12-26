package org.arig.prehistobot.scheduler;

import org.arig.robot.system.RobotManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by gdepuille on 23/12/14.
 */
@Component
public class Schedulers {

    @Autowired
    private RobotManager rm;

    @Scheduled(fixedRate = 10)
    public void robotManagerTask() {
        rm.process();
    }
}
