package org.arig.robot.system.avoiding;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.system.ILidarService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * TODO à reprendre pour faire des tests, vielle implémentation supprimée
 */
@Slf4j
public class AvoidingServiceBouchon implements IAvoidingService {

    @Autowired
    private ILidarService lidarService;

    @Override
    public void process() {
        if (lidarService.mustCleanup()) {
            lidarService.refreshObstacles();
        }
    }

    @Override
    public void setSafeAvoidance(boolean enabled) {

    }
}
