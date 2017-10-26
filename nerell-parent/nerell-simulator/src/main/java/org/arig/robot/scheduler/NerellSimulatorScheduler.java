package org.arig.robot.scheduler;

import org.arig.robot.model.RobotStatus;
import org.arig.robot.services.CalageBordureService;
import org.arig.robot.services.MagasinService;
import org.arig.robot.services.PincesService;
import org.arig.robot.system.ITrajectoryManager;
import org.arig.robot.system.avoiding.IAvoidingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author gdepuille on 30/10/16.
 */
@Component
public class NerellSimulatorScheduler {

    @Autowired
    private RobotStatus rs;

    @Autowired
    private IAvoidingService avoidingService;

    @Scheduled(fixedDelay = 100)
    public void obstacleAvoidanceTask() {
        if (rs.isAvoidanceEnabled()) {
            avoidingService.process();
        }
    }
}
