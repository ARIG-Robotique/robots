package org.arig.robot.scheduler;

import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.communication.balise.enums.DirectionGirouette;
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
    private BaliseService baliseService;

    @Autowired
    private ServosService servosService;

    @Autowired
    private ISystemBlockerManager systemBlockerManager;

    @Scheduled(fixedDelay = 100)
    public void obstacleAvoidanceTask() {
        if (rs.isAvoidanceEnabled()) {
            avoidingService.process();
        }
    }

    @Scheduled(fixedDelay = 2000)
    public void updateBaliseStatus() {
        if (baliseService.isConnected()) {
            baliseService.updateStatus();
        }

        if (rs.isMatchEnabled()) {
            // Lecture Girouette
            if (rs.getElapsedTime() >= IConstantesNerellConfig.baliseElapsedTimeMs && rs.getDirectionGirouette() == DirectionGirouette.UNKNOWN) {
                if (baliseService.isConnected()) {
                    baliseService.lectureGirouette();
                }
            }

            if (!baliseService.isConnected()) {
                baliseService.tryConnect();
            }
        }
    }

    @Scheduled(fixedDelay = 500)
    public void systemBlockerManagerTask() {
        if (rs.isMatchEnabled()) {
            systemBlockerManager.process();
        }
    }

    @Scheduled(fixedDelay = 5000)
    public void systemCheckTensionTaks() {
        if (rs.isMatchEnabled()) {
            servosService.controlBatteryVolts();
        }
    }
}
