package org.arig.robot.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.arig.robot.constants.IConstantesOdinConfig;
import org.arig.robot.filters.common.SignalEdgeFilter;
import org.arig.robot.filters.common.SignalEdgeFilter.Type;
import org.arig.robot.model.OdinRobotStatus;
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.arig.robot.services.AbstractOdinPincesArriereService;
import org.arig.robot.services.AbstractOdinPincesAvantService;
import org.arig.robot.services.OdinEcranService;
import org.arig.robot.system.avoiding.IAvoidingService;
import org.arig.robot.system.blockermanager.ISystemBlockerManager;
import org.arig.robot.system.capteurs.IAlimentationSensor;
import org.arig.robot.system.vacuum.AbstractARIGVacuumController;
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
    private IAlimentationSensor alimentationSensor;

    @Autowired
    protected IMonitoringWrapper monitoringWrapper;

    @Autowired
    private AbstractOdinPincesAvantService pincesAvant;

    @Autowired
    private AbstractOdinPincesArriereService pincesArriere;

    @Autowired
    private AbstractARIGVacuumController vacuumController;

    private StopWatch timerLectureCouleurAvant = new StopWatch();
    private StopWatch timerLectureCouleurArriere = new StopWatch();

    private final SignalEdgeFilter risingEnablePincesAvant = new SignalEdgeFilter(false, Type.RISING);
    private final SignalEdgeFilter risingEnablePincesArriere = new SignalEdgeFilter(false, Type.RISING);

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

//    @Scheduled(fixedDelay = 5000)
//    public void systemCheckTensionTaks() {
//        if (rs.matchEnabled()) {
//            try {
//                alimentationSensor.refresh();
//                AlimentationSensorValue servo = alimentationSensor.get((byte) 0);
//                AlimentationSensorValue moteur = alimentationSensor.get((byte) 0);
//
//            } catch (I2CException e) {
//                log.warn("Erreur de refresh des infos alimentations");
//            }
//        }
//    }

    @Scheduled(fixedDelay = 200)
    public void pincesTask() {
        boolean pincesAvantEnabled = rs.pincesAvantEnabled();
        boolean pincesArriereEnabled = rs.pincesArriereEnabled();

        if (Boolean.TRUE.equals(risingEnablePincesAvant.filter(pincesAvantEnabled))) {
            pincesAvant.activate();
        }
        if (Boolean.TRUE.equals(risingEnablePincesArriere.filter(pincesArriereEnabled))) {
            pincesArriere.activate();
        }

        if (pincesAvantEnabled || pincesArriereEnabled) {
            vacuumController.readAllValues();

            if (pincesAvantEnabled) {
                if(pincesAvant.processBouee()) {
                    timerLectureCouleurAvant.reset();
                    timerLectureCouleurAvant.start();
                }
            }
            if (timerLectureCouleurAvant.getTime(TimeUnit.MILLISECONDS) > IConstantesOdinConfig.TIME_BEFORE_READ_COLOR) {
                timerLectureCouleurAvant.reset();
                pincesAvant.processCouleurBouee();
            }

            if (pincesArriereEnabled) {
                if(pincesArriere.processBouee()) {
                    timerLectureCouleurArriere.reset();
                    timerLectureCouleurArriere.start();
                }
            }
            if (timerLectureCouleurArriere.getTime(TimeUnit.MILLISECONDS) > IConstantesOdinConfig.TIME_BEFORE_READ_COLOR) {
                timerLectureCouleurArriere.reset();
                pincesArriere.processCouleurBouee();
            }
        }
    }
}
