package org.arig.robot.services.avoiding;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.filters.values.DoubleValueAverage;
import org.arig.robot.filters.values.IntegerValueAverage;
import org.arig.robot.model.MonitorPoint;
import org.arig.robot.model.Point;
import org.arig.robot.model.lidar.ScanInfos;
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.arig.robot.system.avoiding.IAvoidingService;
import org.arig.robot.system.capteurs.GP2D12;
import org.arig.robot.system.capteurs.ILidarTelemeter;
import org.arig.robot.system.capteurs.SRF02Sonar;
import org.arig.robot.utils.TableUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author gdepuille on 13/05/15.
 */
@Slf4j
public abstract class AbstractAvoidingService implements IAvoidingService, InitializingBean {

    @Autowired
    private IMonitoringWrapper monitoringWrapper;

    @Autowired
    private TableUtils tableUtils;

    @Autowired
    @Qualifier("gp2dGauche")
    private GP2D12 gp2dGauche;

    @Autowired
    @Qualifier("gp2dCentre")
    private GP2D12 gp2dCentre;

    @Autowired
    @Qualifier("gp2dDroit")
    private GP2D12 gp2dDroit;

    @Autowired
    @Qualifier("usLatGauche")
    private SRF02Sonar usLatGauche;

    @Autowired
    @Qualifier("usGauche")
    private SRF02Sonar usGauche;

    @Autowired
    @Qualifier("usDroit")
    private SRF02Sonar usDroit;

    @Autowired
    @Qualifier("usLatDroit")
    private SRF02Sonar usLatDroit;

    @Autowired
    private ILidarTelemeter lidar;

    // Stockages des points d'obstacles
    @Getter
    private List<Point> detectedPoints = new ArrayList<>();

    @Setter
    @Getter(AccessLevel.PROTECTED)
    private int distanceCentreObstacle = 500;

    private DoubleValueAverage calcAvgGpGauche = new DoubleValueAverage();
    private DoubleValueAverage calcAvgGpCentre = new DoubleValueAverage();
    private DoubleValueAverage calcAvgGpDroit = new DoubleValueAverage();

    private IntegerValueAverage calcAvgUsLatGauche = new IntegerValueAverage();
    private IntegerValueAverage calcAvgUsGauche = new IntegerValueAverage();
    private IntegerValueAverage calcAvgUsDroit = new IntegerValueAverage();
    private IntegerValueAverage calcAvgUsLatDroit = new IntegerValueAverage();

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("Initialisation du service d'évittement d'obstacle");
        usLatGauche.printVersion();
        usGauche.printVersion();
        usDroit.printVersion();
        usLatDroit.printVersion();
        lidar.deviceInfo();
    }

    protected abstract void processAvoiding();

    public final void process() {
        // Lecture GP2D
        Future<GP2D12.GP2D12Values> fGpGauche = gp2dGauche.readValue();
        Future<GP2D12.GP2D12Values> fGpCentre = gp2dCentre.readValue();
        Future<GP2D12.GP2D12Values> fGpDroit = gp2dDroit.readValue();

        // Lecture US
        Future<Integer> fUsLatGauche = usLatGauche.readValue();
        Future<Integer> fUsGauche = usGauche.readValue();
        Future<Integer> fUsDroit = usDroit.readValue();
        Future<Integer> fUsLatDroit = usLatDroit.readValue();

        ScanInfos lidarScan = lidar.grabDatas();

        // TODO : Ajouter un delai pour ne pas rester bloqué.
        while(!fUsLatGauche.isDone() && !fUsGauche.isDone() && !fUsDroit.isDone() && !fUsLatDroit.isDone()
                && !fGpGauche.isDone() && !fGpCentre.isDone() && !fGpDroit.isDone());

        // On efface les anciens points
        detectedPoints.clear();

        double rawGpGauche = GP2D12.INVALID_VALUE, rawGpCentre = GP2D12.INVALID_VALUE, rawGpDroit = GP2D12.INVALID_VALUE;
        double avgGpGauche = 0, avgGpCentre = 0, avgGpDroit = 0;

        int rawUsLatGauche = SRF02Sonar.INVALID_VALUE, rawUsGauche = SRF02Sonar.INVALID_VALUE,
                rawUsDroit = SRF02Sonar.INVALID_VALUE, rawUsLatDroit = SRF02Sonar.INVALID_VALUE;
        int avgUsLatGauche = 0, avgUsGauche = 0, avgUsDroit = 0, avgUsLatDroit = 0;

        try {
            rawGpGauche = fGpGauche.get().getCmValue();
            if (rawGpGauche != GP2D12.INVALID_VALUE) {
                avgGpGauche = calcAvgGpGauche.average(rawGpGauche);
                Point pt = tableUtils.getPointFromAngle(avgGpGauche * 10, 0);
                if (tableUtils.isInTable(pt)) {
                    //detectedPoints.add(pt);
                }
            }
        } catch (InterruptedException | ExecutionException | NullPointerException e) {
            log.warn("Erreur de récupération GP2D Gauche", e);
        }
        try {
            rawGpCentre = fGpCentre.get().getCmValue();
            if (rawGpCentre != GP2D12.INVALID_VALUE) {
                avgGpCentre = calcAvgGpCentre.average(rawGpCentre);
                Point pt = tableUtils.getPointFromAngle(avgGpCentre * 10, 0);
                if (tableUtils.isInTable(pt)) {
                    detectedPoints.add(pt);
                }
            }
        } catch (InterruptedException | ExecutionException | NullPointerException e) {
            log.warn("Erreur de récupération GP2D Centre", e);
        }
        try {
            rawGpDroit = fGpDroit.get().getCmValue();
            if (rawGpDroit != GP2D12.INVALID_VALUE) {
                avgGpDroit = calcAvgGpDroit.average(rawGpDroit);
                Point pt = tableUtils.getPointFromAngle(avgGpDroit * 10, 0);
                if (tableUtils.isInTable(pt)) {
                    //detectedPoints.add(pt);
                }
            }
        } catch (InterruptedException | ExecutionException | NullPointerException e) {
            log.warn("Erreur de récupération GP2D Droit", e);
        }

        try {
            rawUsLatGauche = fUsLatGauche.get();
            if (rawUsLatGauche != SRF02Sonar.INVALID_VALUE) {
                avgUsLatGauche = calcAvgUsLatGauche.average(rawUsLatGauche);
                Point pt = tableUtils.getPointFromAngle(avgUsLatGauche * 10, 0);
                if (tableUtils.isInTable(pt)) {
                    detectedPoints.add(pt);
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            log.warn("Erreur de récupération US lat Gauche", e);
        }
        try {
            rawUsGauche = fUsGauche.get();
            if (rawUsGauche != SRF02Sonar.INVALID_VALUE) {
                avgUsGauche = calcAvgUsGauche.average(rawUsGauche);
                Point pt = tableUtils.getPointFromAngle(avgUsGauche * 10, 0);
                if (tableUtils.isInTable(pt)) {
                    detectedPoints.add(pt);
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            log.warn("Erreur de récupération US Gauche", e);
        }
        try {
            rawUsDroit = fUsDroit.get();
            if (rawUsDroit != SRF02Sonar.INVALID_VALUE) {
                avgUsDroit = calcAvgUsDroit.average(rawUsDroit);
                Point pt = tableUtils.getPointFromAngle(avgUsDroit * 10, 0);
                if (tableUtils.isInTable(pt)) {
                    detectedPoints.add(pt);
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            log.warn("Erreur de récupération US Droit", e);
        }
        try {
            rawUsLatDroit = fUsLatDroit.get();
            if (rawUsLatDroit != SRF02Sonar.INVALID_VALUE) {
                avgUsLatDroit = calcAvgUsLatDroit.average(rawUsLatDroit);
                Point pt = tableUtils.getPointFromAngle(avgUsLatDroit * 10, 0);
                if (tableUtils.isInTable(pt)) {
                    detectedPoints.add(pt);
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            log.warn("Erreur de récupération US lat Droit", e);
        }

        if (lidarScan != null) {
            detectedPoints.addAll(
                lidarScan.getScan().parallelStream()
                    .map(scan -> tableUtils.getPointFromAngle(scan.getDistanceMm(), scan.getAngleDeg()))
                    .filter(pt -> tableUtils.isInTable(pt))
                    .collect(Collectors.toList())
            );
        }

        // Construction du monitoring
        MonitorPoint serie = new MonitorPoint()
                .tableName("avoiding")
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .addField("nbPointDetecte", detectedPoints.size())
                .addField("rawGpGauche", rawGpGauche)
                .addField("avgGpGauche", avgGpGauche)
                .addField("rawGpCentre", rawGpCentre)
                .addField("avgGpCentre", avgGpCentre)
                .addField("rawGpDroit", rawGpDroit)
                .addField("avgGpDroit", avgGpDroit)
                .addField("rawUsLatGauche", rawUsLatGauche)
                .addField("avgUsLatGauche", avgUsLatGauche)
                .addField("rawUsGauche", rawUsGauche)
                .addField("avgUsGauche", avgUsGauche)
                .addField("rawUsDroit", rawUsDroit)
                .addField("avgUsDroit", avgUsDroit)
                .addField("rawUsLatDroit", rawUsLatDroit)
                .addField("avgUsLatDroit", avgUsLatDroit);
        monitoringWrapper.addPoint(serie);

        // 3. Si inclus, on stop et on met a jour le path
        processAvoiding();
    }
}
