package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.system.ITrajectoryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author gdepuille on 07/05/15.
 */
@Slf4j
@Service
public class CalageBordureService {

    @Autowired
    private ITrajectoryManager trajectoryManager;

    @Autowired
    private IIOService ioService;

    public boolean process() {
        if ((ioService.bordureArriereDroite() && ioService.bordureArriereGauche())
                || ioService.bordureAvant()
                || ioService.presenceBaseLunaireDroite() || ioService.presenceBaseLunaireGauche()) {
            trajectoryManager.calageBordureDone();
            return true;
        }
        return false;
    }
}
