package org.arig.robot.scheduler;

import org.arig.robot.model.RobotStatus;
import org.arig.robot.services.BaliseService;
import org.arig.robot.services.CalageBordureService;
import org.arig.robot.services.MagasinService;
import org.arig.robot.services.PincesService;
import org.arig.robot.system.ITrajectoryManager;
import org.arig.robot.system.avoiding.IAvoidingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author gdepuille on 30/10/16.
 */
@Component
public class NerellScheduler {

    @Autowired
    private RobotStatus rs;

    @Autowired
    private IAvoidingService avoidingService;

    @Autowired
    private CalageBordureService calageBordure;

    @Autowired
    private ITrajectoryManager trajectoryManager;

    @Autowired
    private PincesService pincesService;

    @Autowired
    private MagasinService magasinService;

    @Autowired
    private BaliseService baliseService;

    @Scheduled(fixedDelay = 100)
    public void obstacleAvoidanceTask() {
        if (rs.isAvoidanceEnabled()) {
            avoidingService.process();
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

    @Scheduled(fixedDelay = 200)
    public void prisePinceTask() {
        if (rs.isPincesEnabled()) {
            pincesService.enable();
            pincesService.process();
        }
        else {
            pincesService.disable();
        }
    }

    @Scheduled(fixedDelay = 1000)
    public void magasinServiceTask() {
        if (rs.isMagasinServiceEnable()) {
            magasinService.process();
        }
    }

    @Scheduled(fixedDelay = 2000)
    public void updateBaliseStatus() {
        if (baliseService.isConnected()) {
            baliseService.updateStatus();
        }
    }
}
