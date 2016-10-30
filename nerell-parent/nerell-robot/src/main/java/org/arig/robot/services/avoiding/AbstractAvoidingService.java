package org.arig.robot.services.avoiding;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesI2CAdc;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.I2CException;
import org.arig.robot.filters.values.MovingIntegerValueAverage;
import org.arig.robot.model.MonitorPoint;
import org.arig.robot.model.Point;
import org.arig.robot.model.Position;
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.arig.robot.system.avoiding.IAvoidingService;
import org.arig.robot.system.capteurs.I2CAdcAnalogInput;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author gdepuille on 13/05/15.
 */
@Slf4j
public abstract class AbstractAvoidingService implements IAvoidingService, InitializingBean {

    @Autowired
    private ConvertionRobotUnit conv;

    @Autowired
    private IMonitoringWrapper monitoringWrapper;

    @Autowired
    @Qualifier("currentPosition")
    private Position position;

    @Autowired
    private I2CAdcAnalogInput analogInput;

    @Setter
    @Getter(AccessLevel.PROTECTED)
    private int distanceCentreObstacle = 500;

    private static final int distanceDetectionObstacleMm = 300;
    private static final int seuilLateralAvant = 1780;
    private static final int seuilAvant = 1550;

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

            int rawAvantGauche = analogInput.readCapteurValue(IConstantesI2CAdc.GP2D_AVANT_GAUCHE);
            int rawAvantDroit = analogInput.readCapteurValue(IConstantesI2CAdc.GP2D_AVANT_DROIT);
            int rawLateralAvantGauche = analogInput.readCapteurValue(IConstantesI2CAdc.GP2D_AVANT_LATERAL_GAUCHE);
            int rawLateralAvantDroit = analogInput.readCapteurValue(IConstantesI2CAdc.GP2D_AVANT_LATERAL_DROIT);

            int avgAvantGauche = gpAvantGauche.average(rawAvantGauche);
            int avgAvantDroit = gpAvantDroit.average(rawAvantDroit);
            int avgLateralAvantGauche = gpAvantLateralGauche.average(rawLateralAvantGauche);
            int avgLateralAvantDroit = gpAvantLateralDroit.average(rawLateralAvantDroit);

            // Construction du monitoring
            MonitorPoint serie = new MonitorPoint()
                .tableName("avoiding")
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .addField("seuilAvant", seuilAvant)
                .addField("seuilLateralAvant", seuilLateralAvant)
                .addField("rawAvantGauche", rawAvantGauche)
                .addField("avgAvantGauche", avgAvantGauche)
                .addField("rawAvantDroit", rawAvantDroit)
                .addField("avgAvantDroit", avgAvantDroit)
                .addField("rawLateralAvantGauche", rawLateralAvantGauche)
                .addField("avgLateralAvantGauche", avgLateralAvantGauche)
                .addField("rawLateralAvantDroit", rawLateralAvantDroit)
                .addField("avgLateralAvantDroit", avgLateralAvantDroit);
            monitoringWrapper.addPoint(serie);

            if (avgAvantGauche > seuilAvant) {
                Point p = getPointFromAngle(distanceDetectionObstacleMm, 15);
                if (checkPointInTable(p)) {
                    detectedPoints.add(getPointFromAngle(distanceCentreObstacle, 15));
                }
            }

            if (avgAvantDroit > seuilAvant) {
                Point p = getPointFromAngle(distanceDetectionObstacleMm, -15);
                if (checkPointInTable(p)) {
                    detectedPoints.add(getPointFromAngle(distanceCentreObstacle, -15));
                }
            }

            if (avgLateralAvantGauche > seuilLateralAvant) {
                Point p = getPointFromAngle(distanceDetectionObstacleMm, 45);
                if (checkPointInTable(p)) {
                    detectedPoints.add(getPointFromAngle(distanceCentreObstacle, 45));
                }
            }
            if (avgLateralAvantDroit > seuilLateralAvant) {
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
     * @param pt Point correspont à l'obstacle détécté
     * @return true si le point est sur la table
     */
    private boolean checkPointInTable(Point pt) {
        boolean inTable = pt.getX() > IConstantesNerellConfig.minX && pt.getX() < IConstantesNerellConfig.maxX
                && pt.getY() > IConstantesNerellConfig.minY && pt.getY() < IConstantesNerellConfig.maxY;

        boolean inEscalier = pt.getX() > IConstantesNerellConfig.minXEscalier && pt.getX() < IConstantesNerellConfig.maxXEscalier
                && pt.getY() > IConstantesNerellConfig.minYEscalier && pt.getY() < IConstantesNerellConfig.maxYEscalier;

        return inTable && !inEscalier;
    }
}
