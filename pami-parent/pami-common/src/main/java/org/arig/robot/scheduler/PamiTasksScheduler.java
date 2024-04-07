package org.arig.robot.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.PamiRobotStatus;
import org.arig.robot.monitoring.MonitoringWrapper;
import org.arig.robot.services.AbstractEnergyService;
import org.arig.robot.services.BaliseService;
import org.arig.robot.services.IOService;
import org.arig.robot.system.avoiding.AvoidingService;
import org.arig.robot.system.blockermanager.SystemBlockerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PamiTasksScheduler {

    @Autowired
    private PamiRobotStatus rs;

    @Autowired
    private AvoidingService avoidingService;

    @Autowired
    private SystemBlockerManager systemBlockerManager;

    @Autowired
    private BaliseService baliseService;

    @Autowired
    private IOService ioService;

    @Autowired
    private AbstractEnergyService energyService;

    @Autowired
    protected MonitoringWrapper monitoringWrapper;

    @Scheduled(fixedDelay = 20)
    public void obstacleAvoidanceTask() {
        if (rs.avoidanceEnabled()) {
            avoidingService.process();
        }
    }

    @Scheduled(fixedDelay = 500)
    public void systemBlockerManagerTask() {
        if (rs.matchEnabled()) {
            systemBlockerManager.process();
        }
    }

    @Scheduled(fixedDelay = 5000)
    public void systemCheckTensionTaks() {
        if (rs.matchEnabled()) {
            if (!energyService.checkServos()) {
                ioService.disableAlimServos();
            }
            if (!energyService.checkMoteurs()) {
                ioService.disableAlimMoteurs();
            }
        }
    }

    @Scheduled(fixedDelay = 2000)
    public void updateBaliseStatus() {
        if (!rs.baliseEnabled()) {
            return;
        }

        if (!baliseService.isConnected()) {
            baliseService.tryConnect();

        } else {
            if (!rs.twoRobots()) {
                baliseService.startDetection();
            }
            baliseService.updateStatus();
        }
    }
}
