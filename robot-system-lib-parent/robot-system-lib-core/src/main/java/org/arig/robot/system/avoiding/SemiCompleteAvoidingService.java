package org.arig.robot.system.avoiding;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;

import java.util.concurrent.TimeUnit;

/**
 * Evitement semi-temps réel :
 * - prise en compte des obstacles toutes les X secondes
 * - arret sur obstacle et reprise après Y secondes
 */
@Slf4j
public class SemiCompleteAvoidingService extends AbstractAvoidingService {

    private StopWatch stopWatch = new StopWatch();

    private boolean hasProximite = false;

    @Override
    protected void processAvoiding() {
        if (!rs.avoidanceEnabled()) {
            return;
        }

        boolean hasNewProximite = hasProximite();

        if (hasNewProximite) {
            if (!hasProximite) {
                log.info("Attente que l'obstacle sans aille ...");

                hasProximite = true;
                stopWatch.reset();
            }
            long avoidanceWaitTimeMs = rs.avoidanceLong() ? robotConfig.avoidanceWaitTimeLongMs() : robotConfig.avoidanceWaitTimeMs();
            if (stopWatch.getTime(TimeUnit.MILLISECONDS) > avoidanceWaitTimeMs) {
                log.warn("L'obstacle n'est pas parti après {} ms, recherche d'un nouveau chemin", avoidanceWaitTimeMs);

                lidarService.refreshObstacles();
                trajectoryManager.refreshPathFinding();
                trajectoryManager.obstacleNotFound();
                hasProximite = false;
                stopWatch.reset();
            } else {
                trajectoryManager.obstacleFound();
            }
        } else {
            if (hasProximite) {
                log.info("L'obstacle à disparu");

                trajectoryManager.obstacleNotFound();
                hasProximite = false;
                stopWatch.reset();
            }
//            else if (stopWatch.getTime(TimeUnit.MILLISECONDS) > IConstantesNerellConfig.avoidancePathRefreshTimeMs) {
//                log.info("Mise à jour du path");
//
//                lidarService.refreshObstacles();
//                trajectoryManager.refreshPathFinding();
//                stopWatch.reset();
//            }
        }

        if (!stopWatch.isStarted()) {
            stopWatch.start();
        }
    }

}
