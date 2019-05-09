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
import org.arig.robot.utils.ConvertionRobotUnit;
import org.arig.robot.utils.TableUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractAvoidingService implements IAvoidingService, InitializingBean {

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

    @Autowired
    private ILidarTelemeter lidar;

    @Autowired
    private CommandeRobot cmdRobot;

    // Stockages des points d'obstacles
    @Getter
    private final List<Point> detectedPointsMm = new ArrayList<>();
    @Getter
    protected final List<Shape> collisionsShape = new ArrayList<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("Initialisation du service d'évittement d'obstacle");
        lidar.deviceInfo();
    }

    protected abstract void processAvoiding();

    public final void process() {
        ScanInfos lidarScan = lidar.grabDatas();

        // Stockage local des points
        List<Point> detectedPointsMm = new ArrayList<>();

        if (lidarScan != null) {
            detectedPointsMm.addAll(
                    lidarScan.getScan().parallelStream()
                            .map(scan -> tableUtils.getPointFromAngle(scan.getDistanceMm(), scan.getAngleDeg()))
                            .filter(pt -> tableUtils.isInTable(pt) && checkValidPointForSeuil(pt, SEUIL_DISTANCE_AVOID_LIDAR_MM))
                            .collect(Collectors.toList())
            );
        }

        // Construction du monitoring
        MonitorTimeSerie serie = new MonitorTimeSerie()
                .measurementName("avoiding")
                .addTag(MonitorTimeSerie.TAG_NAME, "lidar_top")
                .addField("nbPointLidarDetecte", detectedPointsMm.size());
        monitoringWrapper.addTimeSeriePoint(serie);

        // 3. On delegue à l'implémentation d'évittement
        synchronized (this.detectedPointsMm) {
            this.detectedPointsMm.clear();
            this.detectedPointsMm.addAll(detectedPointsMm);
        }

        processAvoiding();
    }

    protected boolean hasProximite() {
        return getDetectedPointsMm().parallelStream()
                .anyMatch(pt -> checkValidPointForSeuil(pt, SEUIL_DISTANCE_PROX_LIDAR_MM));
    }

    private boolean checkValidPointForSeuil(Point pt, int seuilMm) {
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
