package org.arig.robot.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.OdinRobotStatus;
import org.arig.robot.services.OdinEcranService;
import org.arig.robot.services.OdinServosService;
import org.arig.robot.system.avoiding.IAvoidingService;
import org.arig.robot.system.blockermanager.ISystemBlockerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OdinTasksScheduler {

    @Autowired
    private OdinRobotStatus rs;

    @Autowired
    private IAvoidingService avoidingService;

    @Autowired
    private OdinServosService servosService;

    @Autowired
    private ISystemBlockerManager systemBlockerManager;

    @Autowired
    private OdinEcranService ecranService;

    @Scheduled(fixedRate = 1000)
    public void ecranTask() {
        ecranService.process();
    }

    @Scheduled(fixedDelay = 50)
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
            // TODO Cabler la nouvelle carte
            //servosService.controlBatteryVolts();
        }
    }
}
