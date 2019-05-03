package org.arig.robot.system.motion;

import org.arig.robot.filters.pid.IPidFilter;
import org.arig.robot.filters.ramp.TrapezoidalRampFilter;
import org.arig.robot.model.CommandeRobot;
import org.arig.robot.model.enums.TypeConsigne;
import org.arig.robot.model.monitor.MonitorTimeSerie;
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.arig.robot.system.encoders.Abstract2WheelsEncoders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * The Class AsservissementPolaire.
 *
 * @author gdepuille
 */
public class AsservissementPolaire implements IAsservissementPolaire {

    @Autowired
    protected IMonitoringWrapper monitoringWrapper;

    @Autowired
    private CommandeRobot cmdRobot;

    @Autowired
    private Abstract2WheelsEncoders encoders;

    @Autowired
    @Qualifier("pidDistance")
    private IPidFilter pidDistance;

    @Autowired
    @Qualifier("pidOrientation")
    private IPidFilter pidOrientation;

    @Autowired
    @Qualifier("pidMoteurDroit")
    private IPidFilter pidMoteurDroit;

    @Autowired
    @Qualifier("pidMoteurGauche")
    private IPidFilter pidMoteurGauche;

    @Autowired
    @Qualifier("rampDistance")
    private TrapezoidalRampFilter rampDistance;

    @Autowired
    @Qualifier("rampOrientation")
    private TrapezoidalRampFilter rampOrientation;

    /**
     * Instantiates a new asservissement polaire.
     */
    public AsservissementPolaire() {
        super();
    }

    @Override
    public void reset(final boolean resetFilters) {
        pidDistance.reset();
        pidOrientation.reset();
        pidMoteurDroit.reset();
        pidMoteurGauche.reset();

        if (resetFilters) {
            rampDistance.reset();
            rampOrientation.reset();
        }
    }

    @Override
    public void process() {
        final double positionDistance, distance;
        final double positionOrientation, orientation;

        // Distance
        if (cmdRobot.isType(TypeConsigne.DIST) || cmdRobot.isType(TypeConsigne.XY)) {
            rampDistance.setConsigneVitesse(cmdRobot.getVitesse().getDistance());
            rampDistance.setFrein(cmdRobot.isFrein());
            positionDistance = rampDistance.filter(cmdRobot.getConsigne().getDistance());
            //pidDistance.setConsigne(positionDistance);
            //distance = pidDistance.filter(encoders.getDistance());
        } else {
            positionDistance = distance = 0;
        }

        // Orientation
        if (cmdRobot.isType(TypeConsigne.ANGLE) || cmdRobot.isType(TypeConsigne.XY)) {
            rampOrientation.setConsigneVitesse(cmdRobot.getVitesse().getOrientation());
            rampOrientation.setFrein(true);
            positionOrientation = rampOrientation.filter(cmdRobot.getConsigne().getOrientation());
            //pidOrientation.setConsigne(positionOrientation);
            //orientation = pidOrientation.filter(encoders.getOrientation());
        } else {
            positionOrientation = orientation = 0;
        }

        // Consigne moteurs
        double consigneMotDroit = positionDistance + positionOrientation;
        //double consigneMotDroit = distance + orientation;
        double consigneMotGauche = positionDistance - positionOrientation;
        //double consigneMotGauche = distance - orientation;

        pidMoteurDroit.setConsigne(consigneMotDroit);
        double cmdMotDroit = pidMoteurDroit.filter(encoders.getDroit());

        pidMoteurGauche.setConsigne(consigneMotGauche);
        double cmdMotGauche = pidMoteurGauche.filter(encoders.getGauche());

        cmdRobot.getMoteur().setDroit((int) cmdMotDroit);
        cmdRobot.getMoteur().setGauche((int) cmdMotGauche);

        MonitorTimeSerie serie = new MonitorTimeSerie()
                .measurementName("asservissement")
                .addTag(MonitorTimeSerie.TAG_NAME, "polaire")
                .addField("mot_d", cmdMotDroit)
                .addField("mot_g", cmdMotGauche);

        monitoringWrapper.addTimeSeriePoint(serie);
    }
}
