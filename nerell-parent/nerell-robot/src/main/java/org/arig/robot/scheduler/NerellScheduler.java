package org.arig.robot.scheduler;

import org.arig.robot.filters.common.SignalEdgeFilter;
import org.arig.robot.filters.common.SignalEdgeFilter.Type;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.services.BaliseService;
import org.arig.robot.services.PincesAvantService;
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

    @Autowired
    private PincesAvantService pincesAvant;

    private final SignalEdgeFilter risingEnablePinces = new SignalEdgeFilter(false, Type.RISING);
    private final SignalEdgeFilter fallingEnablePinces = new SignalEdgeFilter(false, Type.FALLING);

    @Scheduled(fixedDelay = 100)
    public void obstacleAvoidanceTask() {
        if (rs.isAvoidanceEnabled()) {
            avoidingService.process();
        }
    }

    @Scheduled(fixedDelay = 200)
    public void pincesAvantTask() {
        boolean pincesEnabled = rs.isPincesEnabled();

        if (risingEnablePinces.filter(pincesEnabled)) {
            pincesAvant.activate();
        }

        if (pincesEnabled) {
            pincesAvant.process();
        }

        if (fallingEnablePinces.filter(pincesEnabled)) {
            pincesAvant.disable();
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
