package org.arig.eurobot.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.arig.eurobot.constants.IConstantesRobot;
import org.arig.eurobot.constants.IConstantesServos;
import org.arig.robot.system.MouvementManager;
import org.arig.robot.system.capteurs.SRF02I2CSonar;
import org.arig.robot.system.pathfinding.IPathFinder;
import org.arig.robot.system.servos.SD21Servos;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.arig.robot.vo.Point;
import org.arig.robot.vo.Position;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by gdepuille on 07/05/15.
 */
@Slf4j
@Service
public class AvoidanceService implements InitializingBean {

    @Autowired
    private ConvertionRobotUnit conv;

    /** The position. */
    @Autowired
    @Qualifier("currentPosition")
    private Position position;

    @Autowired
    private MouvementManager mouvementManager;

    @Autowired
    private IPathFinder pathFinder;

    @Autowired
    private SD21Servos sd21;

    @Autowired
    @Qualifier("usFront")
    private SRF02I2CSonar usFront;

    @Autowired
    @Qualifier("usGauche")
    private SRF02I2CSonar usGauche;

    @Autowired
    @Qualifier("usDroit")
    private SRF02I2CSonar usDroit;

    @Autowired
    @Qualifier("usBack")
    private SRF02I2CSonar usBack;

    private int positionServosCounter = 0;

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("Initialisation du service d'évittement d'obstacle");
        usFront.printVersion();
        usDroit.printVersion();
        usGauche.printVersion();
        usBack.printVersion();
    }

    public void process() {
        switch (positionServosCounter) {
            case 0:
                sd21.setPositionAndWait(IConstantesServos.SONAR, IConstantesServos.SONAR_CENTRE);
                break;
            case 1:
                sd21.setPositionAndWait(IConstantesServos.SONAR, IConstantesServos.SONAR_GAUCHE);
                break;
            case 2:
                sd21.setPositionAndWait(IConstantesServos.SONAR, IConstantesServos.SONAR_CENTRE);
                break;
            case 3:
                sd21.setPositionAndWait(IConstantesServos.SONAR, IConstantesServos.SONAR_DROITE);
                break;
        }

        // Lecture Sonar
        Future<Integer> frontDistance = usFront.readValue();
        Future<Integer> gaucheDistance = usGauche.readValue();
        Future<Integer> droitDistance = usDroit.readValue();
        Future<Integer> backDistance = usBack.readValue();

        while (!(frontDistance.isDone() && gaucheDistance.isDone() && droitDistance.isDone() && backDistance.isDone()));
        try {
            if (frontDistance.get() <= 50) { // US en cm
                log.info("Obstacle detecte");

                // 1. Calcul des coordonées //
                // ------------------------ //

                // 1.A Récupération du point dans le repère cartésien de la table par
                double r = 500; // On place l'obstacle à 50 cm (/!\ Valeur en mm) devant toujours
                double theta = conv.pulseToRad(position.getAngle()); // On calcul la position sur l'angle du repère pour n'avoir que la translation a faire
                Point ptObstacle = new Point();
                ptObstacle.setX(r * Math.cos(theta));
                ptObstacle.setY(r * Math.sin(theta));

                // 1.B Translation du point de la position du robot
                ptObstacle.addDeltaX(conv.pulseToMm(position.getPt().getX()));
                ptObstacle.addDeltaY(conv.pulseToMm(position.getPt().getY()));

                // 2. Controles inclus sur la table
                boolean isInTable = checkPointInTable(ptObstacle);

                // 3. Si inclus, on stop et on met a jour le path
                if (isInTable) {
                    // 3.1 Stop du robot
                    mouvementManager.setObstacleFound(true);

                    // 3.2 Définition de l'obstacle (autour de nous)
                    Polygon polygonObstacle = new Polygon();
                    polygonObstacle.addPoint(-40, 18);
                    polygonObstacle.addPoint(-18, 40);
                    polygonObstacle.addPoint(18, 40);
                    polygonObstacle.addPoint(40, 18);
                    polygonObstacle.addPoint(40, -18);
                    polygonObstacle.addPoint(18, -40);
                    polygonObstacle.addPoint(-18, -40);
                    polygonObstacle.addPoint(-40, -18);
                    polygonObstacle.translate((int) ptObstacle.getX() / 10, (int) ptObstacle.getY() / 10);

                    // 3.3 Mise à jour de la map du path finding
                    pathFinder.addObstacles(polygonObstacle);

                    // 3.4 On relance le bouzin
                    mouvementManager.setRestartAfterObstacle(true);
                } else {
                    log.info("Obstacle en dehors de la table, on ne fait rien");
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            log.error("Erreur lors de la récupération des distances : {}", e.toString());
        }
        //positionServosCounter++;
        if (positionServosCounter > 3) {
            positionServosCounter = 0;
        }
    }

    /**
     * Controle que les coordonnées du point sont sur la table.
     *
     * @param pt
     * @return true si le point est sur la table
     */
    private boolean checkPointInTable(Point pt) {
        return pt.getX() > IConstantesRobot.minX && pt.getX() < IConstantesRobot.maxX
                && pt.getY() > IConstantesRobot.minY && pt.getY() < IConstantesRobot.maxY;
    }
}
