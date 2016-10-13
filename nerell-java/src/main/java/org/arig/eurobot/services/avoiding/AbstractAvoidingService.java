package org.arig.eurobot.services.avoiding;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.arig.eurobot.constants.IConstantesGPIO;
import org.arig.eurobot.constants.IConstantesRobot;
import org.arig.robot.exception.I2CException;
import org.arig.robot.filters.values.MovingIntegerValueAverage;
import org.arig.robot.system.avoiding.IAvoidingService;
import org.arig.robot.system.capteurs.I2CAdcAnalogInput;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.arig.robot.vo.CommandeRobot;
import org.arig.robot.vo.Point;
import org.arig.robot.vo.Position;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.*;

/**
 * @author gdepuille on 13/05/15.
 */
@Slf4j
public abstract class AbstractAvoidingService implements IAvoidingService, InitializingBean {

    @Autowired
    private ConvertionRobotUnit conv;

    /** The position. */
    @Autowired
    @Qualifier("currentPosition")
    private Position position;

    @Autowired
    private CommandeRobot cmdRobot;

    @Autowired
    private I2CAdcAnalogInput analogInput;

    private static final int distanceDetectionObstacleMm = 300;

    @Setter
    @Getter(AccessLevel.PROTECTED)
    private int distanceCentreObstacle = 500;

    private MovingIntegerValueAverage gpAvantGauche = new MovingIntegerValueAverage();
    private MovingIntegerValueAverage gpAvantDroit = new MovingIntegerValueAverage();
    private MovingIntegerValueAverage gpAvantLateralGauche = new MovingIntegerValueAverage();
    private MovingIntegerValueAverage gpAvantLateralDroit = new MovingIntegerValueAverage();

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("Initialisation du service d'évittement d'obstacle");
    }

    public void process() {
        try {
            // Stockages des points d'obstacles
            List<Point> detectedPoints = new ArrayList<>();

            int distanceAvantGauche = gpAvantGauche.average(analogInput.readCapteurValue(IConstantesGPIO.GP2D_AVANT_GAUCHE));
            int distanceAvantDroit = gpAvantDroit.average(analogInput.readCapteurValue(IConstantesGPIO.GP2D_AVANT_DROIT));
            int distanceAvantLateralGauche = gpAvantLateralGauche.average(analogInput.readCapteurValue(IConstantesGPIO.GP2D_AVANT_LATERAL_GAUCHE));
            int distanceAvantLateralDroit = gpAvantLateralDroit.average(analogInput.readCapteurValue(IConstantesGPIO.GP2D_AVANT_LATERAL_DROIT));

            if (distanceAvantGauche > 1550) {
                Point p = getPointFromAngle(distanceDetectionObstacleMm, 15);
                if (checkPointInTable(p)) {
                    detectedPoints.add(getPointFromAngle(distanceCentreObstacle, 15));
                }
            }

            if (distanceAvantDroit > 1550) {
                Point p = getPointFromAngle(distanceDetectionObstacleMm, -15);
                if (checkPointInTable(p)) {
                    detectedPoints.add(getPointFromAngle(distanceCentreObstacle, -15));
                }
            }

            if (distanceAvantLateralGauche > 1780) {
                Point p = getPointFromAngle(distanceDetectionObstacleMm, 45);
                if (checkPointInTable(p)) {
                    detectedPoints.add(getPointFromAngle(distanceCentreObstacle, 45));
                }
            }
            if (distanceAvantLateralDroit > 1780) {
                Point p = getPointFromAngle(distanceDetectionObstacleMm, -45);
                if (checkPointInTable(p)) {
                    detectedPoints.add(getPointFromAngle(distanceCentreObstacle, -45));
                }
            }

            // 3. Si inclus, on stop et on met a jour le path
            processWithPoints(detectedPoints);

        } catch (I2CException e) {
            log.error("Erreur lors de la récupération des distances : {}", e.toString());
        }
    }

    protected abstract void processWithPoints(List<Point> points);

    /**
     * Définition d'un point pour l'obstacle autour du robot.
     *
     * @param angleDeg Valeur en degré sur le robot.
     * @return Le point si présent sur la table, null sinon
     */
    private Point getPointFromAngle(int distanceObstacleMm, double angleDeg) {
        // 1.A Récupération du point dans le repère cartésien de la table par
        double theta = conv.pulseToRad(position.getAngle()) + Math.toRadians(angleDeg); // On calcul la position sur l'angle du repère pour n'avoir que la translation a faire
        Point ptObstacle = new Point();
        ptObstacle.setX(distanceObstacleMm * Math.cos(theta));
        ptObstacle.setY(distanceObstacleMm * Math.sin(theta));

        // 1.B Translation du point de la position du robot
        ptObstacle.addDeltaX(conv.pulseToMm(position.getPt().getX()));
        ptObstacle.addDeltaY(conv.pulseToMm(position.getPt().getY()));

        // 2. Controles inclus sur la table
        return ptObstacle;
    }

    /**
     * Controle que les coordonnées du point sont sur la table.
     *
     * @param pt
     * @return true si le point est sur la table
     */
    private boolean checkPointInTable(Point pt) {
        boolean inTable = pt.getX() > IConstantesRobot.minX && pt.getX() < IConstantesRobot.maxX
                && pt.getY() > IConstantesRobot.minY && pt.getY() < IConstantesRobot.maxY;

        boolean inEscalier = pt.getX() > IConstantesRobot.minXEscalier && pt.getX() < IConstantesRobot.maxXEscalier
                && pt.getY() > IConstantesRobot.minYEscalier && pt.getY() < IConstantesRobot.maxYEscalier;

        return inTable && !inEscalier;
    }
}
