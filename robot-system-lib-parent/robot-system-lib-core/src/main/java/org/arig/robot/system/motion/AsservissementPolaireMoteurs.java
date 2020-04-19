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
 * The Class AsservissementPolaireMoteurs.
 *
 * @author gdepuille
 */
public class AsservissementPolaireMoteurs implements IAsservissementPolaire {

    @Autowired
    protected IMonitoringWrapper monitoringWrapper;

    @Autowired
    private CommandeRobot cmdRobot;

    @Autowired
    private Abstract2WheelsEncoders encoders;

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
    public AsservissementPolaireMoteurs() {
        super();
    }

    @Override
    public void reset(final boolean resetFilters) {
        pidMoteurDroit.reset();
        pidMoteurGauche.reset();

        if (resetFilters) {
            rampDistance.reset();
            rampOrientation.reset();
        }
    }

    @Override
    public void process() {
        final double positionDistance, positionOrientation;

        // Distance
        if (cmdRobot.isType(TypeConsigne.DIST) || cmdRobot.isType(TypeConsigne.XY)) {
            rampDistance.setConsigneVitesse(cmdRobot.getVitesse().getDistance());
            rampDistance.setFrein(cmdRobot.isFrein());
            positionDistance = rampDistance.filter(cmdRobot.getConsigne().getDistance());
        } else {
            positionDistance = 0;
        }

        // Orientation
        if (cmdRobot.isType(TypeConsigne.ANGLE) || cmdRobot.isType(TypeConsigne.XY)) {
            rampOrientation.setConsigneVitesse(cmdRobot.getVitesse().getOrientation());
            rampOrientation.setFrein(true);
            positionOrientation = rampOrientation.filter(cmdRobot.getConsigne().getOrientation());
        } else {
            positionOrientation = 0;
        }

        // Consigne moteurs
        double consigneMotDroit = positionDistance + positionOrientation;
        double consigneMotGauche = positionDistance - positionOrientation;

        pidMoteurDroit.consigne(consigneMotDroit);
        double cmdMotDroit = pidMoteurDroit.filter(encoders.getDroit());

        pidMoteurGauche.consigne(consigneMotGauche);
        double cmdMotGauche = pidMoteurGauche.filter(encoders.getGauche());

        cmdRobot.getMoteur().setDroit((int) cmdMotDroit);
        cmdRobot.getMoteur().setGauche((int) cmdMotGauche);

        final MonitorTimeSerie serie = new MonitorTimeSerie()
                .measurementName("asservissement")
                .addTag(MonitorTimeSerie.TAG_NAME, "polaire")
                .addField("mot_d", cmdMotDroit)
                .addField("mot_g", cmdMotGauche);

        monitoringWrapper.addTimeSeriePoint(serie);
    }
}
