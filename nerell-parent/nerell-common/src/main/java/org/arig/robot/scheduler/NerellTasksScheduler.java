package org.arig.robot.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.arig.robot.constants.IEurobotConfig;
import org.arig.robot.constants.INerellConstantesConfig;
import org.arig.robot.filters.common.SignalEdgeFilter;
import org.arig.robot.filters.common.SignalEdgeFilter.Type;
import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.model.communication.balise.enums.EDirectionGirouette;
import org.arig.robot.services.AbstractEnergyService;
import org.arig.robot.services.BaliseService;
import org.arig.robot.services.INerellIOService;
import org.arig.robot.services.INerellPincesArriereService;
import org.arig.robot.services.INerellPincesAvantService;
import org.arig.robot.services.NerellEcranService;
import org.arig.robot.system.avoiding.IAvoidingService;
import org.arig.robot.system.blockermanager.ISystemBlockerManager;
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
    private INerellIOService ioService;

    @Autowired
    private ISystemBlockerManager systemBlockerManager;

    @Autowired
    private NerellEcranService ecranService;

    @Autowired
    private BaliseService baliseService;

    @Autowired
    private AbstractEnergyService energyService;

    @Autowired
    private INerellPincesAvantService pincesAvant;

    @Autowired
    private INerellPincesArriereService pincesArriere;


    private StopWatch timerLectureCouleur = new StopWatch();

    private final SignalEdgeFilter risingEnablePinces = new SignalEdgeFilter(false, Type.RISING);
    private final SignalEdgeFilter fallingForcePinces = new SignalEdgeFilter(false, Type.FALLING);
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
                //baliseService.lectureCouleurEcueilEquipe();
            }
        }
    }

    @Scheduled(fixedDelay = 200)
    public void pincesTask() {
        boolean pincesAvantEnabled = rs.pincesAvantEnabled();
        boolean pincesAvantForceMode = rs.pincesAvantForceOn();

        if (Boolean.TRUE.equals(risingEnablePinces.filter(pincesAvantEnabled))) {
            pincesAvant.activate();
        } else if (Boolean.TRUE.equals(fallingEnablePinces.filter(pincesAvantEnabled))) {
            pincesAvant.deactivate();
        }

        if (Boolean.TRUE.equals(fallingForcePinces.filter(pincesAvantForceMode)) && pincesAvantEnabled) {
            ioService.enableAllPompes();
        }

        if (pincesAvantEnabled) {
            if (pincesAvant.processBouee()) {
                timerLectureCouleur.reset();
                timerLectureCouleur.start();
            }

            if (timerLectureCouleur.getTime(TimeUnit.MILLISECONDS) > INerellConstantesConfig.TIME_BEFORE_READ_COLOR) {
                timerLectureCouleur.reset();
                pincesAvant.processCouleurBouee();
            }
        }

        if (rs.matchRunning()) {
            pincesArriere.processCouleurBouee();
        }
    }
}
