package org.arig.robot.system.motion;

import lombok.AccessLevel;
import lombok.Getter;
import org.arig.robot.model.Position;
import org.arig.robot.model.enums.TypeOdometrie;
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.influxdb.dto.Point;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.concurrent.TimeUnit;

/**
 * The Class AbstractOdometrie.
 *
 * @author gdepuille
 */
public abstract class AbstractOdometrie implements IOdometrie, InitializingBean {

    @Autowired
    private IMonitoringWrapper monitoringWrapper;

    /**
     * The position.
     */
    @Autowired
    @Qualifier("currentPosition")
    @Getter(AccessLevel.PROTECTED)
    private Position position;

    @Autowired
    private ConvertionRobotUnit conv;

    /**
     * The type.
     */
    @Getter
    private final TypeOdometrie type;

    /**
     * Instantiates a new odometrie.
     *
     * @param type the type
     */
    protected AbstractOdometrie(final TypeOdometrie type) {
        this.type = type;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        initOdometrie(0, 0, 0);
    }

    /**
     * Inits the odometrie.
     *
     * @param x     the x
     * @param y     the y
     * @param angle the angle
     */
    @Override
    public void initOdometrie(final double x, final double y, final int angle) {
        position.updatePosition(x, y, angle);
    }

    /**
     * Process.
     */
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
        Point serie = Point.measurement("odometrie")
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .addField("X", conv.pulseToMm(getPosition().getPt().getX()))
                .addField("Y", conv.pulseToMm(getPosition().getPt().getY()))
                .addField("angle", conv.pulseToDeg(getPosition().getAngle()))
                .addField("type", type.ordinal())
                .build();

        monitoringWrapper.addPoint(serie);
    }
}
