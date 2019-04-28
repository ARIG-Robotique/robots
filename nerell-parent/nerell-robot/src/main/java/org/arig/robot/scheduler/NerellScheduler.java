package org.arig.robot.scheduler;

import org.arig.robot.exceptions.PinceNotAvailableException;
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
    private PincesService pincesService;

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
    public void prisePinceDroiteTask() {
        if (rs.isServicesMetierEnabled()) {
            try {
                if (rightSideService.buteePalet() && rightSideService.presencePalet() && !pincesService.isWorking(ESide.DROITE)) {
                    pincesService.waitAvailable(ESide.DROITE);

                    if (pincesService.priseTable(CouleurPalet.INCONNU, ESide.DROITE)) {
                        pincesService.stockageAsync(ESide.DROITE);
                    }
                }
            } catch (PinceNotAvailableException e) {

            }
        }
    }

    @Scheduled(fixedDelay = 1000)
    public void prisePinceGaucheTask() {
        if (rs.isServicesMetierEnabled()) {
            try {
                if (leftSideService.buteePalet() && leftSideService.presencePalet() && !pincesService.isWorking(ESide.GAUCHE)) {
                    pincesService.waitAvailable(ESide.GAUCHE);

                    if (pincesService.priseTable(CouleurPalet.INCONNU, ESide.GAUCHE)) {
                        pincesService.stockageAsync(ESide.GAUCHE);
                    }
                }
            } catch (PinceNotAvailableException e) {

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
