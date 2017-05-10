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
import org.arig.robot.system.capteurs.SRF02Sonar;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
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

//    @Autowired
//    @Qualifier("usLatGauche")
//    private SRF02Sonar usLatGauche;
//
//    @Autowired
//    @Qualifier("usGauche")
//    private SRF02Sonar usGauche;
//
//    @Autowired
//    @Qualifier("usDroit")
//    private SRF02Sonar usDroit;
//
//    @Autowired
//    @Qualifier("usLatDroit")
//    private SRF02Sonar usLatDroit;

    @Setter
    @Getter(AccessLevel.PROTECTED)
    private int distanceCentreObstacle = 500;

    private static final int distanceDetectionObstacleMm = 300;
    private static final int seuilLateralAvant = 1780;
    private static final int seuilAvant = 1550;

    private MovingIntegerValueAverage calcAvgGpGauche = new MovingIntegerValueAverage();
    private MovingIntegerValueAverage calcAvgGpCentre = new MovingIntegerValueAverage();
    private MovingIntegerValueAverage calcAvgGpDroit = new MovingIntegerValueAverage();

    private MovingIntegerValueAverage calcAvgUsLatGauche = new MovingIntegerValueAverage();
    private MovingIntegerValueAverage calcAvgUsGauche = new MovingIntegerValueAverage();
    private MovingIntegerValueAverage calcAvgUsDroit = new MovingIntegerValueAverage();
    private MovingIntegerValueAverage calcAvgUsLatDroit = new MovingIntegerValueAverage();

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("Initialisation du service d'évittement d'obstacle");
//        usLatGauche.printVersion();
//        usGauche.printVersion();
//        usDroit.printVersion();
//        usLatDroit.printVersion();
    }

    public void process() {
        try {
            // Stockages des points d'obstacles
            List<Point> detectedPoints = new ArrayList<>();

            // Lecture US
//            Future<Integer> fUsLatGauche = usLatGauche.readValue();
//            Future<Integer> fUsGauche = usGauche.readValue();
//            Future<Integer> fUsDroit = usDroit.readValue();
//            Future<Integer> fUsLatDroit = usLatDroit.readValue();

            // Lecture GP2D
            // FIXME GP2D12 Class a faire pour renvoyer une valeur en cm ISO avec les SRF02
            int rawGpGauche = analogInput.readCapteurValue(IConstantesI2CAdc.GP2D_AVANT_GAUCHE);
            int rawGpCentre = analogInput.readCapteurValue(IConstantesI2CAdc.GP2D_AVANT_CENTRE);
            int rawGpDroit = analogInput.readCapteurValue(IConstantesI2CAdc.GP2D_AVANT_DROIT);

//            while(!fUsLatGauche.isDone() && !fUsGauche.isDone() && !fUsDroit.isDone() && !fUsLatDroit.isDone());
//            int rawUsLatGauche = -1, rawUsGauche = -1, rawUsDroit = -1, rawUsLatDroit = -1;
//            try {
//                rawUsLatGauche = fUsLatGauche.get();
//            } catch (InterruptedException | ExecutionException e) {
//                log.warn("Erreur de récupération US lat Gauche", e);
//            }
//            try {
//                rawUsGauche = fUsGauche.get();
//            } catch (InterruptedException | ExecutionException e) {
//                log.warn("Erreur de récupération US Gauche", e);
//            }
//            try {
//                rawUsDroit = fUsDroit.get();
//            } catch (InterruptedException | ExecutionException e) {
//                log.warn("Erreur de récupération US Droit", e);
//            }
//            try {
//                rawUsLatDroit = fUsLatDroit.get();
//            } catch (InterruptedException | ExecutionException e) {
//                log.warn("Erreur de récupération US lat Droit", e);
//            }

            // Filtrage des valeurs
            int avgGpGauche = calcAvgGpGauche.average(rawGpGauche);
            int avgGpCentre = calcAvgGpCentre.average(rawGpCentre);
            int avgGpDroit = calcAvgGpDroit.average(rawGpDroit);
//            int avgUsLatGauche = calcAvgUsLatGauche.average(rawUsLatGauche);
//            int avgUsGauche = calcAvgUsLatGauche.average(rawUsGauche);
//            int avgUsDroit = calcAvgUsLatGauche.average(rawUsDroit);
//            int avgUsLatDroit = calcAvgUsLatGauche.average(rawUsLatDroit);

            // Construction du monitoring
            MonitorPoint serie = new MonitorPoint()
                .tableName("avoiding")
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .addField("rawGpGauche", rawGpGauche)
                .addField("avgGpGauche", avgGpGauche)
                .addField("rawGpCentre", rawGpCentre)
                .addField("avgGpCentre", avgGpCentre)
                .addField("rawGpDroit", rawGpDroit)
                .addField("avgGpDroit", avgGpDroit);
//                .addField("rawUsLatGauche", rawUsLatGauche)
//                .addField("avgUsLatGauche", avgUsLatGauche)
//                .addField("rawUsGauche", rawUsGauche)
//                .addField("avgUsGauche", avgUsGauche)
//                .addField("rawUsDroit", rawUsDroit)
//                .addField("avgUsDroit", avgUsDroit)
//                .addField("rawUsLatDroit", rawUsLatDroit)
//                .addField("avgUsLatDroit", avgUsLatDroit);
            monitoringWrapper.addPoint(serie);

            if (avgGpCentre > seuilAvant) {
                Point p = getPointFromAngle(distanceDetectionObstacleMm, 15);
                if (checkPointInTable(p)) {
                    detectedPoints.add(getPointFromAngle(distanceCentreObstacle, 15));
                }
            }

            if (avgGpDroit > seuilAvant) {
                Point p = getPointFromAngle(distanceDetectionObstacleMm, -15);
                if (checkPointInTable(p)) {
                    detectedPoints.add(getPointFromAngle(distanceCentreObstacle, -15));
                }
            }

            if (avgGpGauche > seuilLateralAvant) {
                Point p = getPointFromAngle(distanceDetectionObstacleMm, 45);
                if (checkPointInTable(p)) {
                    detectedPoints.add(getPointFromAngle(distanceCentreObstacle, 45));
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

//        boolean inEscalier = pt.getX() > IConstantesNerellConfig.minXEscalier && pt.getX() < IConstantesNerellConfig.maxXEscalier
//                && pt.getY() > IConstantesNerellConfig.minYEscalier && pt.getY() < IConstantesNerellConfig.maxYEscalier;
//
//        return inTable && !inEscalier;

        return inTable;
    }
}
