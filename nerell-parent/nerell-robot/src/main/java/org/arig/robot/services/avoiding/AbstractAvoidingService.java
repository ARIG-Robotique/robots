package org.arig.robot.services.avoiding;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.CommandeRobot;
import org.arig.robot.model.Point;
import org.arig.robot.model.Position;
import org.arig.robot.model.Shape;
import org.arig.robot.model.lidar.ScanInfos;
import org.arig.robot.model.monitor.MonitorTimeSerie;
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.arig.robot.system.avoiding.IAvoidingService;
import org.arig.robot.system.capteurs.ILidarTelemeter;
import org.arig.robot.system.capteurs.SRF02Sonar;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.arig.robot.utils.TableUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author gdepuille on 13/05/15.
 */
@Slf4j
public abstract class AbstractAvoidingService implements IAvoidingService, InitializingBean {

    protected static final int SEUIL_DISTANCE_PROX_CAPTEURS_MM = 200;
    protected static final int SEUIL_DISTANCE_PROX_LIDAR_MM = 450;
    protected static final int SEUIL_DISTANCE_AVOID_LIDAR_MM = 600;

    @Autowired
    private IMonitoringWrapper monitoringWrapper;

    @Autowired
    private TableUtils tableUtils;

    @Autowired
    private ConvertionRobotUnit conv;

    @Autowired
    @Qualifier("currentPosition")
    private Position position;

    /*
    @Autowired
    @Qualifier("gp2dGauche")
    private GP2D12 gp2dGauche;

    @Autowired
    @Qualifier("gp2dCentre")
    private GP2D12 gp2dCentre;

    @Autowired
    @Qualifier("gp2dDroit")
    private GP2D12 gp2dDroit;
*/

    @Autowired
    @Qualifier("usLatGauche")
    private SRF02Sonar usLatGauche;

    @Autowired
    @Qualifier("usGauche")
    private SRF02Sonar usGauche;

    /*
    @Autowired
    @Qualifier("usDroit")
    private SRF02Sonar usDroit;
    */

    @Autowired
    @Qualifier("usLatDroit")
    private SRF02Sonar usLatDroit;

    @Autowired
    private ILidarTelemeter lidar;

    @Autowired
    private CommandeRobot cmdRobot;

    // Stockages des points d'obstacles
    @Getter
    private final List<Point> detectedPointsMmCapteurs = new ArrayList<>();
    @Getter
    private final List<Point> detectedPointsMmLidar = new ArrayList<>();
    @Getter
    protected final List<Shape> collisionsShape = new ArrayList<>();

    /*private DoubleValueAverage calcAvgGpGauche = new DoubleValueAverage();
    private DoubleValueAverage calcAvgGpCentre = new DoubleValueAverage();
    private DoubleValueAverage calcAvgGpDroit = new DoubleValueAverage();*/

    /*private IntegerValueAverage calcAvgUsLatGauche = new IntegerValueAverage();
    private IntegerValueAverage calcAvgUsGauche = new IntegerValueAverage();
    private IntegerValueAverage calcAvgUsDroit = new IntegerValueAverage();
    private IntegerValueAverage calcAvgUsLatDroit = new IntegerValueAverage();*/

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("Initialisation du service d'évittement d'obstacle");
        usLatGauche.printVersion();
        usGauche.printVersion();
        //usDroit.printVersion();
        usLatDroit.printVersion();
        lidar.deviceInfo();
    }

    protected abstract void processAvoiding();

    public final void process() {
        // Lecture GP2D
        /*Future<GP2D12.GP2D12Values> fGpGauche = gp2dGauche.readValue();
        Future<GP2D12.GP2D12Values> fGpCentre = gp2dCentre.readValue();
        Future<GP2D12.GP2D12Values> fGpDroit = gp2dDroit.readValue();*/

        // Lecture US
        /*
        Future<Integer> fUsLatGauche = usLatGauche.readValue();
        Future<Integer> fUsGauche = usGauche.readValue();
        Future<Integer> fUsDroit = usDroit.readValue();
        Future<Integer> fUsLatDroit = usLatDroit.readValue();
        */

        ScanInfos lidarScan = lidar.grabDatas();

        // TODO : Ajouter un delai pour ne pas rester bloqué.
        /*while(!fUsLatGauche.isDone() && !fUsGauche.isDone() && !fUsDroit.isDone() && !fUsLatDroit.isDone()
                && !fGpGauche.isDone() && !fGpCentre.isDone() && !fGpDroit.isDone());*/
        //while (!fGpGauche.isDone() && !fGpCentre.isDone() && !fGpDroit.isDone()) ;

        // Stockage local des points
        List<Point> detectedPointsMmCapteurs = new ArrayList<>();
        List<Point> detectedPointsMmLidar = new ArrayList<>();

        /*double rawGpGauche = GP2D12.INVALID_VALUE, rawGpCentre = GP2D12.INVALID_VALUE, rawGpDroit = GP2D12.INVALID_VALUE;
        double avgGpGauche = 0, avgGpCentre = 0, avgGpDroit = 0;

        int rawUsLatGauche = SRF02Sonar.INVALID_VALUE, rawUsGauche = SRF02Sonar.INVALID_VALUE,
                rawUsDroit = SRF02Sonar.INVALID_VALUE, rawUsLatDroit = SRF02Sonar.INVALID_VALUE;
        int avgUsLatGauche = 0, avgUsGauche = 0, avgUsDroit = 0, avgUsLatDroit = 0;

        try {
            rawGpGauche = fGpGauche.get().getMmValue();
            if (rawGpGauche != GP2D12.INVALID_VALUE) {
                avgGpGauche = calcAvgGpGauche.average(rawGpGauche);
                Point pt = tableUtils.getPointFromAngle(avgGpGauche, 10, 68, 158);
                if (tableUtils.isInTable(pt)) {
                    detectedPointsMmCapteurs.add(pt);
                }
            }
        } catch (InterruptedException | ExecutionException | NullPointerException e) {
            log.warn("Erreur de récupération GP2D Gauche", e);
        }
        try {
            rawGpCentre = fGpCentre.get().getMmValue();
            if (rawGpCentre != GP2D12.INVALID_VALUE) {
                avgGpCentre = calcAvgGpCentre.average(rawGpCentre);
                Point pt = tableUtils.getPointFromAngle(avgGpCentre, 0, 125, 0);
                if (tableUtils.isInTable(pt)) {
                    detectedPointsMmCapteurs.add(pt);
                }
            }
        } catch (InterruptedException | ExecutionException | NullPointerException e) {
            log.warn("Erreur de récupération GP2D Centre", e);
        }
        try {
            rawGpDroit = fGpDroit.get().getMmValue();
            if (rawGpDroit != GP2D12.INVALID_VALUE) {
                avgGpDroit = calcAvgGpDroit.average(rawGpDroit);
                Point pt = tableUtils.getPointFromAngle(avgGpDroit, -10, 68, -158);
                if (tableUtils.isInTable(pt)) {
                    detectedPointsMmCapteurs.add(pt);
                }
            }
        } catch (InterruptedException | ExecutionException | NullPointerException e) {
            log.warn("Erreur de récupération GP2D Droit", e);
        }*/

        /*
        try {
            rawUsLatGauche = fUsLatGauche.get();
            if (rawUsLatGauche != SRF02Sonar.INVALID_VALUE) {
                avgUsLatGauche = calcAvgUsLatGauche.average(rawUsLatGauche);
                Point pt = tableUtils.getPointFromAngle(avgUsLatGauche, 15, 115,120);
                if (tableUtils.isInTable(pt)) {
                    detectedPointsMmCapteurs.add(pt);
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            log.warn("Erreur de récupération US lat Gauche", e);
        }
        try {
            rawUsGauche = fUsGauche.get();
            if (rawUsGauche != SRF02Sonar.INVALID_VALUE) {
                avgUsGauche = calcAvgUsGauche.average(rawUsGauche);
                Point pt = tableUtils.getPointFromAngle(avgUsGauche, -7, 155, 70);
                if (tableUtils.isInTable(pt)) {
                    detectedPointsMmCapteurs.add(pt);
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            log.warn("Erreur de récupération US Gauche", e);
        }
        try {
            rawUsDroit = fUsDroit.get();
            if (rawUsDroit != SRF02Sonar.INVALID_VALUE) {
                avgUsDroit = calcAvgUsDroit.average(rawUsDroit);
                Point pt = tableUtils.getPointFromAngle(avgUsDroit, 2, 155, -70);
                if (tableUtils.isInTable(pt)) {
                    detectedPointsMmCapteurs.add(pt);
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            log.warn("Erreur de récupération US Droit", e);
        }
        try {
            rawUsLatDroit = fUsLatDroit.get();
            if (rawUsLatDroit != SRF02Sonar.INVALID_VALUE) {
                avgUsLatDroit = calcAvgUsLatDroit.average(rawUsLatDroit);
                Point pt = tableUtils.getPointFromAngle(avgUsLatDroit, -15, 115, 120);
                if (tableUtils.isInTable(pt)) {
                    detectedPointsMmCapteurs.add(pt);
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            log.warn("Erreur de récupération US lat Droit", e);
        }
        */

        if (lidarScan != null) {
            detectedPointsMmLidar.addAll(
                    lidarScan.getScan().parallelStream()
                            .map(scan -> tableUtils.getPointFromAngle(scan.getDistanceMm(), scan.getAngleDeg()))
                            .filter(pt -> tableUtils.isInTable(pt) && checkValidLidarPointForSeuil(pt, SEUIL_DISTANCE_AVOID_LIDAR_MM))
                            .collect(Collectors.toList())
            );
        }

        // Construction du monitoring
        MonitorTimeSerie serie = new MonitorTimeSerie()
                .tableName("avoiding")
                //.addField("nbPointCapteursDetecte", detectedPointsMmCapteurs.size())
                .addField("nbPointLidarDetecte", detectedPointsMmLidar.size());
                /*.addField("rawGpGauche", rawGpGauche)
                .addField("avgGpGauche", avgGpGauche)
                .addField("rawGpCentre", rawGpCentre)
                .addField("avgGpCentre", avgGpCentre)
                .addField("rawGpDroit", rawGpDroit)
                .addField("avgGpDroit", avgGpDroit);*/
                /*.addField("rawUsLatGauche", rawUsLatGauche)
                .addField("avgUsLatGauche", avgUsLatGauche)
                .addField("rawUsGauche", rawUsGauche)
                .addField("avgUsGauche", avgUsGauche)
                .addField("rawUsDroit", rawUsDroit)
                .addField("avgUsDroit", avgUsDroit)
                .addField("rawUsLatDroit", rawUsLatDroit)
                .addField("avgUsLatDroit", avgUsLatDroit);*/
        monitoringWrapper.addTimeSeriePoint(serie);

        // 3. On delegue à l'implémentation d'évittement
        synchronized (this.detectedPointsMmCapteurs) {
            this.detectedPointsMmCapteurs.clear();
            this.detectedPointsMmCapteurs.addAll(detectedPointsMmCapteurs);
        }
        synchronized (this.detectedPointsMmLidar) {
            this.detectedPointsMmLidar.clear();
            this.detectedPointsMmLidar.addAll(detectedPointsMmLidar);
        }
        processAvoiding();
    }

    protected boolean hasProximiteCapteurs() {
        return getDetectedPointsMmCapteurs().parallelStream()
                .anyMatch(pt -> checkValidCapteursPointForSeuil(pt, SEUIL_DISTANCE_PROX_CAPTEURS_MM));
    }

    protected boolean hasProximiteLidar() {
        return getDetectedPointsMmLidar().parallelStream()
                .anyMatch(pt -> checkValidLidarPointForSeuil(pt, SEUIL_DISTANCE_PROX_LIDAR_MM));
    }

    /*
    protected boolean hasAvoidLidar() {
        return getDetectedPointsMmLidar().parallelStream()
                .anyMatch(pt -> checkValidLidarPointForSeuil(pt, SEUIL_DISTANCE_AVOID_LIDAR_MM));
    }
    */

    private boolean checkValidCapteursPointForSeuil(Point pt, int seuilMm) {
        long dX = (long) (pt.getX() - conv.pulseToMm(position.getPt().getX()));
        long dY = (long) (pt.getY() - conv.pulseToMm(position.getPt().getY()));
        double distanceMm = Math.sqrt(Math.pow(dX, 2) + Math.pow(dY, 2));
        return distanceMm < seuilMm;
    }

    private boolean checkValidLidarPointForSeuil(Point pt, int seuilMm) {
        long dX = (long) (pt.getX() - conv.pulseToMm(position.getPt().getX()));
        long dY = (long) (pt.getY() - conv.pulseToMm(position.getPt().getY()));
        double distanceMm = Math.sqrt(Math.pow(dX, 2) + Math.pow(dY, 2));
        if (distanceMm > seuilMm) {
            return false;
        }

        double alpha = Math.toDegrees(Math.atan2(Math.toRadians(dY), Math.toRadians(dX)));
        double dA = alpha - conv.pulseToDeg(position.getAngle());
        if (dA > 180) {
            dA -= 360;
        } else if (dA < -180) {
            dA += 360;
        }

        if (cmdRobot.getConsigne().getDistance() > 0) {
            return dA > -45 && dA < 45;
        } else {
            return Math.abs(dA) < 180 && Math.abs(dA) > 135;
        }
    }
}
