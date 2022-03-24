package org.arig.robot.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.arig.robot.constants.NerellConstantesConfig;
import org.arig.robot.filters.common.ChangeFilter;
import org.arig.robot.filters.common.SignalEdgeFilter;
import org.arig.robot.filters.common.SignalEdgeFilter.Type;
import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.services.AbstractEnergyService;
import org.arig.robot.services.BaliseService;
import org.arig.robot.services.NerellEcranService;
import org.arig.robot.services.NerellIOService;
import org.arig.robot.services.NerellPincesAvantService;
import org.arig.robot.system.avoiding.AvoidingService;
import org.arig.robot.system.blockermanager.SystemBlockerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class NerellTasksScheduler {

    @Autowired
    private NerellRobotStatus rs;

    @Autowired
    private AvoidingService avoidingService;

    @Autowired
    private NerellIOService ioService;

    @Autowired
    private SystemBlockerManager systemBlockerManager;

    @Autowired
    private NerellEcranService ecranService;

    @Autowired
    private BaliseService baliseService;

    @Autowired
    private AbstractEnergyService energyService;

    @Autowired
    private NerellPincesAvantService pincesAvant;

    private final StopWatch timerLectureCouleur = new StopWatch();

    private final SignalEdgeFilter risingEnablePinces = new SignalEdgeFilter(false, Type.RISING);
    private final ChangeFilter<Boolean> changeModeForce = new ChangeFilter<>(false);
    private final SignalEdgeFilter fallingEnablePinces = new SignalEdgeFilter(false, Type.FALLING);

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
            baliseService.startDetection();
            baliseService.updateStatus();
        }
    }

    @Scheduled(fixedDelay = 200)
    public void pincesTask() {
        boolean pincesAvantEnabled = rs.pincesAvantEnabled();
        boolean pincesAvantForceMode = rs.pincesAvantForceOn();

        if (Boolean.TRUE.equals(risingEnablePinces.filter(pincesAvantEnabled))
            || (pincesAvantEnabled && changeModeForce.filter(pincesAvantForceMode))) {
            pincesAvant.activate();
        } else if (Boolean.TRUE.equals(fallingEnablePinces.filter(pincesAvantEnabled))) {
            pincesAvant.deactivate();
        }

        if (pincesAvantEnabled) {
            if (pincesAvant.process()) {
                timerLectureCouleur.reset();
                timerLectureCouleur.start();
            }

            if (timerLectureCouleur.getTime(TimeUnit.MILLISECONDS) > NerellConstantesConfig.TIME_BEFORE_READ_COLOR) {
                timerLectureCouleur.reset();
                pincesAvant.processCouleur();
            }
        }
    }
}
