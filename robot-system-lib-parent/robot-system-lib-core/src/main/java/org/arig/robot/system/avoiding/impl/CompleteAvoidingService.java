package org.arig.robot.system.avoiding.impl;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.TypeMouvement;
import org.arig.robot.model.monitor.AbstractMonitorMouvement;
import org.arig.robot.model.monitor.MonitorMouvementPath;
import org.arig.robot.system.avoiding.AbstractAvoidingService;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Evitement complet avec recalcul en temps réel du path si obstacle détecté
 * Ne fonctionne que sur simulateur
 */
@Slf4j
public class CompleteAvoidingService extends AbstractAvoidingService {

    private boolean hasObstacle = false;

    private AbstractMonitorMouvement currentMvt;

    private List<Line2D> lines = new ArrayList<>(); // Chemin parcouru

    @Override
    protected void processAvoiding() {
        checkMouvement();
        lidarService.refreshObstacles(lines);

        // Une collision est détecté
        if (lidarService.hasObstacle()) {
            hasObstacle = true;

            // On rafraichit le path
            if (rs.avoidanceEnabled()) {
                trajectoryManager.refreshPathFinding();
            }

        } else if (hasObstacle) {
            hasObstacle = false;

            // On rafraichit le path
            if (rs.avoidanceEnabled()) {
                trajectoryManager.refreshPathFinding();
            }
        }
    }

    protected void checkMouvement() {
        // Construction du chemin a parcourir sur le changement de mouvement
        if (currentMvt != trajectoryManager.getCurrentMouvement()) {
            log.info("Le mouvement courant a changé");
            currentMvt = trajectoryManager.getCurrentMouvement();

            lines.clear();
            if (currentMvt.getType() == TypeMouvement.PATH) {
                MonitorMouvementPath mp = (MonitorMouvementPath) currentMvt;

                Point2D ptFrom = new Point2D.Double(
                        conv.pulseToMm(currentPosition.getPt().getX()) / 10,
                        conv.pulseToMm(currentPosition.getPt().getY()) / 10
                );
                Point2D ptTo;
                for (Point pt : mp.getPath()) {
                    ptTo = new Point2D.Double(
                            pt.getX() / 10,
                            pt.getY() / 10
                    );

                    lines.add(new Line2D.Double(ptFrom, ptTo));
                    ptFrom = new Point2D.Double(ptTo.getX(), ptTo.getY());
                }
            }
        }
    }

}
