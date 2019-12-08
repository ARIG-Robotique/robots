package org.arig.robot.services.avoiding;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.model.Cercle;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.TypeMouvement;
import org.arig.robot.model.monitor.AbstractMonitorMouvement;
import org.arig.robot.model.monitor.MonitorMouvementPath;
import org.arig.robot.system.pathfinding.IPathFinder;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.*;
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

    @Autowired
    private IPathFinder pathFinder;

    private boolean hasObstacle = false;

    private AbstractMonitorMouvement currentMvt;

    private int checkSumLidar = 0; // Checksum des coordonnées lidar pour detecter les changements

    private List<Line2D> lines = new ArrayList<>(); // Chemin parcouru

    private List<java.awt.Shape> tmpObstacles = new ArrayList<>(); // Obstacles pour le pathfinding

    private List<org.arig.robot.model.Shape> tmpCollisionsShape = new ArrayList<>(); // Obstacles pour le superviseur

    @Override
    protected void processAvoiding() {
        checkMouvement();
        checkObstacles();

        // Affichage des zones d'ombre sur le superviseur
        synchronized (this.collisionsShape) {
            this.collisionsShape.clear();
            this.collisionsShape.addAll(tmpCollisionsShape);
        }

        // Une collision est détecté
        if (CollectionUtils.isNotEmpty(tmpObstacles)) {
            setObstacles(tmpObstacles);

        } else if (hasObstacle) {
            clearOstacles();
        }
    }

    protected boolean checkMouvement() {
        boolean mouvementHasChanged = false;

        // Construction du chemin a parcourir sur le changement de mouvement
        if (currentMvt != trajectoryManager.getCurrentMouvement()) {
            log.info("Le mouvement courant a changé");
            mouvementHasChanged = true;
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

        return mouvementHasChanged;
    }

    protected boolean checkObstacles() {
        boolean obstaclesHasChanged = false;

        // Si les points lidar on changé on check
        int checkSumLidar = getDetectedPointsMm().hashCode();

        if (checkSumLidar != this.checkSumLidar) {
            this.checkSumLidar = checkSumLidar;
            obstaclesHasChanged = true;

            tmpObstacles.clear();
            tmpCollisionsShape.clear();

            pointLidar:
            for (Point pt : AvoidingUtils.calculateCenterObs(getDetectedPointsMm())) {
                tmpCollisionsShape.add(new Cercle(pt, IConstantesNerellConfig.pathFindingSeuilProximite));

                Rectangle obstacle = new Rectangle(
                        (int) Math.round(pt.getX() / 10. - IConstantesNerellConfig.pathFindingSeuilProximite / 10. / 2.),
                        (int) Math.round(pt.getY() / 10. - IConstantesNerellConfig.pathFindingSeuilProximite / 10. / 2.),
                        (int) Math.round(IConstantesNerellConfig.pathFindingSeuilProximite / 10.),
                        (int) Math.round(IConstantesNerellConfig.pathFindingSeuilProximite / 10.)
                );

                for (Line2D l : lines) {
                    if (l.intersects(obstacle)) {
                        log.info("Collision détectée, ajout polygon : {} {}", pt, obstacle.toString());
                        tmpObstacles.add(new Rectangle(
                                (int) Math.round(pt.getX() / 10. - IConstantesNerellConfig.pathFindingTailleObstacle / 10. / 2.),
                                (int) Math.round(pt.getY() / 10. - IConstantesNerellConfig.pathFindingTailleObstacle / 10. / 2.),
                                (int) Math.round(IConstantesNerellConfig.pathFindingTailleObstacle / 10.),
                                (int) Math.round(IConstantesNerellConfig.pathFindingTailleObstacle / 10.)
                        ));
                        continue pointLidar;
                    }
                }
            }
        }

        return obstaclesHasChanged;
    }

    private void setObstacles(List<java.awt.Shape> obstacles) {
        hasObstacle = true;
        pathFinder.setObstacles(obstacles.toArray(new java.awt.Shape[0]));

        // On rafraichit le path
        if (rs.isAvoidanceEnabled()) {
            trajectoryManager.refreshPathFinding();
        }
    }

    private void clearOstacles() {
        hasObstacle = false;
        pathFinder.setObstacles();

        // On rafraichit le path
        if (rs.isAvoidanceEnabled()) {
            trajectoryManager.refreshPathFinding();
        }
    }
}
