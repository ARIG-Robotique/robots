package org.arig.robot.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.PamiRobotStatus;
import org.arig.robot.monitoring.MonitoringWrapper;
import org.arig.robot.services.AbstractEnergyService;
import org.arig.robot.services.BaliseService;
import org.arig.robot.services.IOService;
import org.arig.robot.services.PamiEcranService;
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

    @Autowired(required = false)
    private SystemBlockerManager systemBlockerManager;

    @Autowired
    private PamiEcranService ecranService;

    @Autowired
    private BaliseService baliseService;

    @Autowired
    private IOService ioService;

    @Autowired
    private AbstractEnergyService energyService;

    @Autowired
    protected MonitoringWrapper monitoringWrapper;

    @Scheduled(fixedRate = 1000)
    public void ecranTask() {
        ecranService.process();
    }

    @Scheduled(fixedDelay = 20)
    public void obstacleAvoidanceTask() {
        if (rs.avoidanceEnabled()) {
            avoidingService.process();
        }
    }

    @Scheduled(fixedDelay = 500)
    public void systemBlockerManagerTask() {
        if (systemBlockerManager != null && rs.matchEnabled() && !rs.simulateur()) {
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
}
