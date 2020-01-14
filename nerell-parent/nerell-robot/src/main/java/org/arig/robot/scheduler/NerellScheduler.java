package org.arig.robot.scheduler;

import org.arig.robot.model.RobotStatus;
import org.arig.robot.services.BaliseService;
import org.arig.robot.services.ServosService;
import org.arig.robot.system.ITrajectoryManager;
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
    private ITrajectoryManager trajectoryManager;

    @Autowired
    private BaliseService baliseService;

    @Autowired
    private ServosService servosService;

    @Autowired
    private ISystemBlockerManager systemBlockerManager;

    @Scheduled(fixedDelay = 100)
    public void obstacleAvoidanceTask() {
        if (rs.isMatchEnabled()) {
            avoidingService.process();
        }
    }

    @Scheduled(fixedDelay = 2000)
    public void updateBaliseStatus() {
        if (baliseService.isConnected()) {
            baliseService.updateStatus();
        }
    }

    @Scheduled(fixedDelay = 500)
    public void systemBlockerManagerTask() {
        systemBlockerManager.process();
    }

    @Scheduled(fixedDelay = 5000)
    public void systemCheckTensionTaks() {
        servosService.controlBatteryVolts();
    }

}
