package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.system.MouvementManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author gdepuille on 07/05/15.
 */
@Slf4j
@Service
public class CalageBordureService {

    @Autowired
    private MouvementManager mouvementManager;

    @Autowired
    private IOService ioService;

    public void process() {
        if (ioService.buteeAvantDroit() && ioService.buteeAvantGauche()) {
            mouvementManager.setObstacleFound(true);
            mouvementManager.setRestartAfterObstacle(true);
        }
    }
}
