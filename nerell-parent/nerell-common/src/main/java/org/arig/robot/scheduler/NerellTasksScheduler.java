package org.arig.robot.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.constants.IEurobotConfig;
import org.arig.robot.filters.common.SignalEdgeFilter;
import org.arig.robot.filters.common.SignalEdgeFilter.Type;
import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.model.communication.balise.enums.EDirectionGirouette;
import org.arig.robot.services.AbstractNerellPincesAvantService;
import org.arig.robot.services.BaliseService;
import org.arig.robot.services.NerellEcranService;
import org.arig.robot.services.NerellServosService;
import org.arig.robot.system.avoiding.IAvoidingService;
import org.arig.robot.system.blockermanager.ISystemBlockerManager;
import org.arig.robot.system.vacuum.AbstractARIGVacuumController;
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
    private IAvoidingService avoidingService;

    @Autowired
    private NerellServosService servosService;

    @Autowired
    private ISystemBlockerManager systemBlockerManager;

    @Autowired
    private NerellEcranService ecranService;

    @Autowired
    private BaliseService baliseService;

    @Autowired
    private AbstractNerellPincesAvantService pincesAvant;

    @Autowired
    private AbstractARIGVacuumController vacuumController;

    private StopWatch timerLectureCouleur = new StopWatch();

    private final SignalEdgeFilter risingEnablePinces = new SignalEdgeFilter(false, Type.RISING);

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
            servosService.controlBatteryVolts();
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

            if (rs.matchEnabled()) {
                // Lecture Girouette
                if (rs.getElapsedTime() >= IEurobotConfig.baliseElapsedTimeMs && rs.directionGirouette() == EDirectionGirouette.UNKNOWN) {
                    baliseService.lectureGirouette();
                }

                baliseService.lectureCouleurBouees();
                baliseService.lectureEcueilAdverse();
                baliseService.lectureHautFond();
            } else {
                // Lecture couleur écueil
                baliseService.lectureCouleurEcueilEquipe();
            }
        }
    }

    @Scheduled(fixedDelay = 200)
    public void pincesAvantTask() {
        boolean pincesEnabled = rs.pincesAvantEnabled();

        if (Boolean.TRUE.equals(risingEnablePinces.filter(pincesEnabled))) {
            pincesAvant.activate();
        }

        if (pincesEnabled) {
            vacuumController.readAllValues();
            if (pincesAvant.processBouee()) {
                timerLectureCouleur.reset();
                timerLectureCouleur.start();
            }

            if (timerLectureCouleur.getTime(TimeUnit.MILLISECONDS) > IConstantesNerellConfig.lectureCouleurWaitTimeMs) {
                timerLectureCouleur.reset();
                pincesAvant.processCouleurBouee();
            }
        }
    }
}
