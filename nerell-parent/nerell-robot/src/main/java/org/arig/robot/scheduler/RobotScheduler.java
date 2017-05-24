package org.arig.robot.scheduler;

import org.arig.robot.model.RobotStatus;
import org.arig.robot.services.CalageBordureService;
import org.arig.robot.services.MagasinService;
import org.arig.robot.services.PincesService;
import org.arig.robot.services.ServosService;
import org.arig.robot.system.avoiding.IAvoidingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author gdepuille on 30/10/16.
 */
@Component
public class RobotScheduler {

    @Autowired
    private RobotStatus rs;

    @Autowired
    private IAvoidingService avoidingService;

    @Autowired
    private CalageBordureService calageBordure;

    @Autowired
    private PincesService pincesService;

    @Autowired
    private MagasinService magasinService;



    @Scheduled(fixedDelay = 100)
    public void obstacleAvoidanceTask() {
        if (rs.isAvoidanceEnabled()) {
            avoidingService.process();
        }
    }

    @Scheduled(fixedDelay = 200)
    public void calageBordureTask() {
        if (rs.isCalageBordureEnabled()) {
            calageBordure.process();
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
}
