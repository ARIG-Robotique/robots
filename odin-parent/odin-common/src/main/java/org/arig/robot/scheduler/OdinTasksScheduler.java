package org.arig.robot.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.arig.robot.constants.IOdinConstantesConfig;
import org.arig.robot.filters.common.SignalEdgeFilter;
import org.arig.robot.filters.common.SignalEdgeFilter.Type;
import org.arig.robot.model.OdinRobotStatus;
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.arig.robot.services.AbstractEnergyService;
import org.arig.robot.services.AbstractOdinPincesArriereService;
import org.arig.robot.services.AbstractOdinPincesAvantService;
import org.arig.robot.services.IIOService;
import org.arig.robot.services.OdinEcranService;
import org.arig.robot.system.avoiding.IAvoidingService;
import org.arig.robot.system.blockermanager.ISystemBlockerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class OdinTasksScheduler {

    @Autowired
    private OdinRobotStatus rs;

    @Autowired
    private IAvoidingService avoidingService;

    @Autowired
    private ISystemBlockerManager systemBlockerManager;

    @Autowired
    private OdinEcranService ecranService;

    @Autowired
    private IIOService ioService;

    @Autowired
    private AbstractEnergyService energyService;

    @Autowired
    protected IMonitoringWrapper monitoringWrapper;

    @Autowired
    private AbstractOdinPincesAvantService pincesAvant;

    @Autowired
    private AbstractOdinPincesArriereService pincesArriere;

    private StopWatch timerLectureCouleurAvant = new StopWatch();
    private StopWatch timerLectureCouleurArriere = new StopWatch();

    private final SignalEdgeFilter risingEnablePincesAvant = new SignalEdgeFilter(false, Type.RISING);
    private final SignalEdgeFilter fallingEnablePincesAvant = new SignalEdgeFilter(false, Type.FALLING);
    private final SignalEdgeFilter risingEnablePincesArriere = new SignalEdgeFilter(false, Type.RISING);
    private final SignalEdgeFilter fallingEnablePincesArriere = new SignalEdgeFilter(false, Type.FALLING);

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

    @Scheduled(fixedDelay = 200)
    public void pincesTask() {
        boolean pincesAvantEnabled = rs.pincesAvantEnabled();
        boolean pincesArriereEnabled = rs.pincesArriereEnabled();

        if (Boolean.TRUE.equals(risingEnablePincesAvant.filter(pincesAvantEnabled))) {
            pincesAvant.activate();
        } else if (Boolean.TRUE.equals(fallingEnablePincesAvant.filter(pincesAvantEnabled))) {
            pincesAvant.deactivate();
        }

        if (Boolean.TRUE.equals(risingEnablePincesArriere.filter(pincesArriereEnabled))) {
            pincesArriere.activate();
        } else if (Boolean.TRUE.equals(fallingEnablePincesArriere.filter(pincesArriereEnabled))) {
            pincesArriere.deactivate();
        }

        if (pincesAvantEnabled || pincesArriereEnabled) {
            if (pincesAvantEnabled) {
                if(pincesAvant.process()) {
                    timerLectureCouleurAvant.reset();
                    timerLectureCouleurAvant.start();
                }
            }
            if (timerLectureCouleurAvant.getTime(TimeUnit.MILLISECONDS) > IOdinConstantesConfig.TIME_BEFORE_READ_COLOR) {
                timerLectureCouleurAvant.reset();
                pincesAvant.processCouleur();
            }

            if (pincesArriereEnabled) {
                if(pincesArriere.process()) {
                    timerLectureCouleurArriere.reset();
                    timerLectureCouleurArriere.start();
                }
            }
            if (timerLectureCouleurArriere.getTime(TimeUnit.MILLISECONDS) > IOdinConstantesConfig.TIME_BEFORE_READ_COLOR) {
                timerLectureCouleurArriere.reset();
                pincesArriere.processCouleur();
            }
        }
    }
}
