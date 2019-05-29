package org.arig.robot.scheduler;

import org.arig.robot.constants.IConstantesUtiles;
import org.arig.robot.exceptions.VentouseNotAvailableException;
import org.arig.robot.model.ESide;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.enums.CouleurPalet;
import org.arig.robot.services.*;
import org.arig.robot.system.ICarouselManager;
import org.arig.robot.system.ITrajectoryManager;
import org.arig.robot.system.avoiding.IAvoidingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Component
public class NerellScheduler {

    @Autowired
    private RobotStatus rs;

    @Autowired
    private ICarouselManager carousel;

    @Autowired
    private IAvoidingService avoidingService;

    @Autowired
    private ITrajectoryManager trajectoryManager;

    @Autowired
    private IVentousesService ventousesService;

    @Autowired
    private CarouselService carouselService;

    @Autowired
    private SerrageService serrageService;

    @Autowired
    private MagasinService magasinService;

    @Autowired
    private BaliseService baliseService;

    @Autowired
    private RightSideService rightSideService;

    @Autowired
    private LeftSideService leftSideService;

    @Autowired
    private ServosService servosService;

//    @Autowired
//    private ISystemBlockerManager systemBlockerManager;

    @Scheduled(fixedDelay = 100)
    public void obstacleAvoidanceTask() {
        if (rs.isMatchEnabled()) {
            avoidingService.process();
        }
    }

    @Scheduled(fixedDelay = 2000)
    public void updateBaliseStatus() {
        if (baliseService.isConnected()) {
            baliseService.updateStatus();
        }
    }

    @Scheduled(fixedDelay = 1000)
    public void priseVentouseDroiteTask() {
        if (rs.isVentousesEnabled()) {
            try {
                if (rightSideService.buteePalet() && rightSideService.presencePalet() && !ventousesService.isWorking(ESide.DROITE) && carousel.has(null)) {
                    ventousesService.waitAvailable(ESide.DROITE);

                    if (ventousesService.priseTable(CouleurPalet.INCONNU, ESide.DROITE)) {
                        ventousesService.stockageCarousel(ESide.DROITE);
                    } else {
                        ventousesService.finishDepose(ESide.DROITE).get();
                    }
                }
            } catch (VentouseNotAvailableException | InterruptedException | ExecutionException e) {

            }
        }
    }

    @Scheduled(fixedDelay = 1000)
    public void priseVentouseGaucheTask() {
        if (rs.isVentousesEnabled()) {
            try {
                if (leftSideService.buteePalet() && leftSideService.presencePalet() && !ventousesService.isWorking(ESide.GAUCHE) && carousel.has(null)) {
                    ventousesService.waitAvailable(ESide.GAUCHE);

                    if (ventousesService.priseTable(CouleurPalet.INCONNU, ESide.GAUCHE)) {
                        ventousesService.stockageCarousel(ESide.GAUCHE);
                    } else {
                        ventousesService.finishDepose(ESide.GAUCHE).get();
                    }
                }
            } catch (VentouseNotAvailableException | InterruptedException | ExecutionException e) {

            }
        }
    }

    @Scheduled(fixedDelay = 500)
    public void carouselTask() {
        if (rs.isCarouselEnabled()) {
            carouselService.lectureCouleur();
            carouselService.positionIdeale();
        }
    }

    @Scheduled(fixedDelay = 500)
    public void serrageTask() {
        if (rs.isSerrageEnabled()) {
            serrageService.process();
        }
    }

    @Scheduled(fixedDelay = 1000)
    public void magasinTask() {
        if (rs.isMagasinEnabled()) {
//            magasinService.process();
        }
    }

//    @Scheduled(fixedDelay = 1000)
//    public void systemBlockerManagerTask() {
//        systemBlockerManager.process();
//    }

    @Scheduled(fixedDelay = 5000)
    public void systemCheckTensionTaks() {
        servosService.controlBatteryVolts();
    }

}
