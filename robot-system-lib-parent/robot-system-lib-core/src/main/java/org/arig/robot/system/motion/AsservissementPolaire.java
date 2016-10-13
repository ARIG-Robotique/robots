package org.arig.robot.system.motion;

import org.arig.robot.filters.pid.IPidFilter;
import org.arig.robot.filters.ramp.IRampFilter;
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.arig.robot.system.encoders.Abstract2WheelsEncoders;
import org.arig.robot.vo.CommandeRobot;
import org.arig.robot.vo.enums.TypeConsigne;
import org.influxdb.dto.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.concurrent.TimeUnit;

/**
 * The Class AsservissementPolaire.
 *
 * @author gdepuille
 */
public class AsservissementPolaire implements IAsservissementPolaire {

    @Autowired
    private IMonitoringWrapper monitoringWrapper;

    /**
     * The commande robot.
     */
    @Autowired
    private CommandeRobot cmdRobot;

    /**
     * The encoders.
     */
    @Autowired
    private Abstract2WheelsEncoders encoders;

    /**
     * The pid distance.
     */
    @Autowired
    @Qualifier("pidDistance")
    private IPidFilter pidDistance;

    /**
     * The pid orientation.
     */
    @Autowired
    @Qualifier("pidOrientation")
    private IPidFilter pidOrientation;

    /**
     * The filter distance.
     */
    @Autowired
    @Qualifier("rampDistance")
    private IRampFilter rampDistance;

    /**
     * The filter orientation.
     */
    @Autowired
    @Qualifier("rampOrientation")
    private IRampFilter rampOrientation;

    /**
     * The output distance.
     */
    private double outputDistance;

    /**
     * The output orientation.
     */
    private double outputOrientation;

    /**
     * Instantiates a new asservissement polaire.
     */
    public AsservissementPolaire() {
        super();
    }

    @Override
    public void reset() {
        reset(false);
    }

    @Override
    public void reset(final boolean resetFilters) {
        pidDistance.reset();
        pidOrientation.reset();

        if (resetFilters) {
            rampDistance.reset();
            rampOrientation.reset();
        }
    }

    @Override
    public void process() {
        // Application du filtre pour la génération du profil trapézoidale et définition des consignes
        // de distance pour le mode DIST ou XY
        if (cmdRobot.isType(TypeConsigne.DIST) || cmdRobot.isType(TypeConsigne.XY)) {
            double setPointDistance = rampDistance.filter(cmdRobot.getVitesse().getDistance(), cmdRobot.getConsigne().getDistance(), cmdRobot.isFrein());
            outputDistance = pidDistance.compute(setPointDistance, encoders.getDistance());
        } else {
            outputDistance = 0;
        }
        // Toujours le frein pour l'orientation
        if (cmdRobot.isType(TypeConsigne.ANGLE) || cmdRobot.isType(TypeConsigne.XY)) {
            double setPointOrientation = rampOrientation.filter(cmdRobot.getVitesse().getOrientation(), cmdRobot.getConsigne().getOrientation(), true);
            outputOrientation = pidOrientation.compute(setPointOrientation, encoders.getOrientation());
        } else {
            outputOrientation = 0;
        }

        // Consigne moteurs
        cmdRobot.getMoteur().setDroit((int) (outputDistance + outputOrientation));
        cmdRobot.getMoteur().setGauche((int) (outputDistance - outputOrientation));

        sendMonitoring();
    }

    private void sendMonitoring() {
        // Construction du monitoring
        Point serie = Point.measurement("asserv_polaire")
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .addField("outDistance", outputDistance)
                .addField("outOrientation", outputOrientation)
                .addField("cmdMotG", cmdRobot.getMoteur().getGauche())
                .addField("cmdMotD", cmdRobot.getMoteur().getDroit())
                .build();

        monitoringWrapper.write(serie);
    }
}
