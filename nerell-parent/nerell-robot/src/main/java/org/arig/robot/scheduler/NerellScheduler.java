package org.arig.robot.scheduler;

import org.arig.robot.exceptions.VentouseNotAvailableException;
import org.arig.robot.model.ESide;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.enums.CouleurPalet;
import org.arig.robot.services.BaliseService;
import org.arig.robot.services.CalageBordureService;
import org.arig.robot.services.CarouselService;
import org.arig.robot.services.LeftSideService;
import org.arig.robot.services.MagasinService;
import org.arig.robot.services.RightSideService;
import org.arig.robot.services.SerrageService;
import org.arig.robot.services.VentousesService;
import org.arig.robot.system.ICarouselManager;
import org.arig.robot.system.ITrajectoryManager;
import org.arig.robot.system.avoiding.IAvoidingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NerellScheduler {

    @Autowired
    private RobotStatus rs;

    @Autowired
    private ICarouselManager carousel;

    @Autowired
    private IAvoidingService avoidingService;

    @Autowired
    private CalageBordureService calageBordure;

    @Autowired
    private ITrajectoryManager trajectoryManager;

    @Autowired
    private VentousesService ventousesService;

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
    }

    @Scheduled(fixedDelay = 200)
    public void calageBordureTask() {
        if (rs.isCalageBordureEnabled()) {
            if (calageBordure.process() || trajectoryManager.isTrajetAtteint() || trajectoryManager.isTrajetEnApproche()) {
                // Calage effectué, on arrete
                rs.disableCalageBordure();
            }
        }
    }

    @Scheduled(fixedDelay = 1000)
    public void priseVentouseDroiteTask() {
        if (rs.isVentousesEnabled()) {
            try {
                if (rightSideService.buteePalet() && rightSideService.presencePalet() && !ventousesService.isWorking(ESide.DROITE)) {
                    ventousesService.waitAvailable(ESide.DROITE);

                    if (ventousesService.priseTable(CouleurPalet.INCONNU, ESide.DROITE)) {
                        ventousesService.stockageAsync(ESide.DROITE);
                    } else {
                        ventousesService.finishDeposeAsync(ESide.DROITE);
                    }
                }
            } catch (VentouseNotAvailableException e) {

            }
        }
    }

    @Scheduled(fixedDelay = 1000)
    public void priseVentouseGaucheTask() {
        if (rs.isVentousesEnabled()) {
            try {
                if (leftSideService.buteePalet() && leftSideService.presencePalet() && !ventousesService.isWorking(ESide.GAUCHE)) {
                    ventousesService.waitAvailable(ESide.GAUCHE);

                    if (ventousesService.priseTable(CouleurPalet.INCONNU, ESide.GAUCHE)) {
                        ventousesService.stockageAsync(ESide.GAUCHE);
                    } else {
                        ventousesService.finishDeposeAsync(ESide.GAUCHE);
                    }
                }
            } catch (VentouseNotAvailableException e) {

            }
        }
    }

    @Scheduled(fixedDelay = 500)
    public void carouselTask() {
        if (rs.isServicesMetierEnabled()) {
            if (carousel.has(CouleurPalet.INCONNU) && !carouselService.isWorking()) {
                carouselService.lectureCouleurAsync(carousel.firstIndexOf(CouleurPalet.INCONNU, ICarouselManager.LECTEUR));
            }
        }
    }

    @Scheduled(fixedDelay = 500)
    public void serrageTask() {
        if (rs.isServicesMetierEnabled()) {
            serrageService.process();
        }
    }

    @Scheduled(fixedDelay = 1000)
    public void magasinTask() {
        if (rs.isServicesMetierEnabled()) {
            magasinService.process();
        }
    }

}
