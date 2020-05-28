package org.arig.robot.scheduler;

import org.arig.robot.model.NerellStatus;
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
    private NerellStatus rs;

    @Autowired
    private IAvoidingService avoidingService;

    @Scheduled(fixedDelay = 100)
    public void obstacleAvoidanceTask() {
        if (rs.isAvoidanceEnabled()) {
            avoidingService.process();
        }
    }
}
