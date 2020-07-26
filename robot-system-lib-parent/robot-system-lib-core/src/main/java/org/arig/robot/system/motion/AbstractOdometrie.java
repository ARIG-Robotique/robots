package org.arig.robot.system.motion;

import lombok.Getter;
import org.arig.robot.model.Point;
import org.arig.robot.model.Position;
import org.arig.robot.model.enums.TypeOdometrie;
import org.arig.robot.model.monitor.MonitorTimeSerie;
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * The Class AbstractOdometrie.
 *
 * @author gdepuille
 */
public abstract class AbstractOdometrie implements IOdometrie, InitializingBean {

    @Autowired
    private IMonitoringWrapper monitoringWrapper;

    @Autowired
    @Qualifier("currentPosition")
    @Getter
    private Position currentPosition;

    protected final Position corrigePosition = new Position();

    @Autowired
    private ConvertionRobotUnit conv;

    @Getter
    private final TypeOdometrie type;

    protected AbstractOdometrie(final TypeOdometrie type) {
        this.type = type;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        updatePosition(0, 0);
        updateAngle(0);
    }

    @Override
    public void updatePosition(final double x, final double y) {
        updateX(x);
        updateY(y);
    }

    @Override
    public void updatePosition(final Point pt) {
        updateX(pt.getX());
        updateY(pt.getY());
    }

    @Override
    public void updateX(final double x) {
        currentPosition.getPt().setX(x);
        corrigePosition.getPt().setX(x);
    }

    @Override
    public void updateY(final double y) {
        currentPosition.getPt().setY(y);
        corrigePosition.getPt().setY(y);
    }

    @Override
    public void updateAngle(final double angle) {
        currentPosition.setAngle(angle);
        corrigePosition.setAngle(angle);
    }

    protected abstract void process();

    /**
     * Calcul de la position en fonction de la valeurs des codeurs.
     * <p>
     * /!\ Cette méthode doit être appelé après la lecture des valeurs codeurs toutes les x ms.
     */
    @Override
    public void calculPosition() {
        process();
        sendMonitoring();
    }

    private void sendMonitoring() {
        // Construction du monitoring
        MonitorTimeSerie serie = new MonitorTimeSerie()
                .measurementName("odometrie")
                .addField("xCorr", conv.pulseToMm(corrigePosition.getPt().getX()))
                .addField("yCorr", conv.pulseToMm(corrigePosition.getPt().getY()))
                .addField("angleCorr", conv.pulseToDeg(corrigePosition.getAngle()))
                .addField("x", conv.pulseToMm(getCurrentPosition().getPt().getX()))
                .addField("y", conv.pulseToMm(getCurrentPosition().getPt().getY()))
                .addField("angle", conv.pulseToDeg(getCurrentPosition().getAngle()))
                .addField("type", type.ordinal());

        monitoringWrapper.addTimeSeriePoint(serie);
    }
}
