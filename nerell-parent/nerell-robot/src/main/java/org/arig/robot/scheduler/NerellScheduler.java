package org.arig.robot.scheduler;

import org.arig.robot.model.RobotStatus;
import org.arig.robot.services.BaliseService;
import org.arig.robot.services.ServosService;
import org.arig.robot.system.avoiding.IAvoidingService;
import org.arig.robot.system.blockermanager.ISystemBlockerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NerellScheduler {

    @Autowired
    private RobotStatus rs;

    @Autowired
    private IAvoidingService avoidingService;

    @Autowired
    private ServosService servosService;

    @Scheduled(fixedDelay = 100)
    public void obstacleAvoidanceTask() {
        if (rs.isAvoidanceEnabled()) {
            avoidingService.process();
        }
    }

    @Scheduled(fixedDelay = 5000)
    public void systemCheckTensionTaks() {
        if (rs.isMatchEnabled()) {
            servosService.controlBatteryVolts();
        }
    }
}
