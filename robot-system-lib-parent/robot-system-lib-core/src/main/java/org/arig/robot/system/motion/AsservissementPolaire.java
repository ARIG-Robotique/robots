package org.arig.robot.system.motion;

import org.arig.robot.filters.pid.IPidFilter;
import org.arig.robot.filters.ramp.IRampFilter;
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
    private IMonitoringWrapper monitoringWrapper;

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
    private IRampFilter rampDistance;

    @Autowired
    @Qualifier("rampOrientation")
    private IRampFilter rampOrientation;

    private double outputDistance;
    private double outputOrientation;
    private double positionDistance;
    private double positionOrientation;

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
        pidMoteurDroit.reset();
        pidMoteurGauche.reset();

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
            positionDistance = rampDistance.filter(cmdRobot.getVitesse().getDistance(), cmdRobot.getConsigne().getDistance(), encoders.getDistance(), cmdRobot.isFrein());
            outputDistance = pidDistance.compute(positionDistance, encoders.getDistance());
        } else {
            outputDistance = positionDistance = 0;
        }
        // Génération consigne pour l'orientation
        if (cmdRobot.isType(TypeConsigne.ANGLE) || cmdRobot.isType(TypeConsigne.XY)) {
            positionOrientation = rampOrientation.filter(cmdRobot.getVitesse().getOrientation(), cmdRobot.getConsigne().getOrientation(), encoders.getOrientation(), true);
            outputOrientation = pidOrientation.compute(positionOrientation, encoders.getOrientation());
        } else {
            outputOrientation = positionOrientation = 0;
        }

        // Consigne moteurs
//        double cmdMotDroit = pidMoteurDroit.compute(positionDistance + positionOrientation, encoders.getDroit());
//        double cmdMotGauche = pidMoteurGauche.compute(positionDistance - positionOrientation, encoders.getGauche());
        double cmdMotDroit = outputDistance + outputOrientation;
        double cmdMotGauche = outputDistance - outputOrientation;
        cmdRobot.getMoteur().setDroit((int) cmdMotDroit);
        cmdRobot.getMoteur().setGauche((int) cmdMotGauche);

        sendMonitoring();
    }

    private void sendMonitoring() {
        // Construction du monitoring
        MonitorTimeSerie serie = new MonitorTimeSerie()
                .tableName("asserv_polaire")
                .addField("outputDistance", outputDistance)
                .addField("outputOrientation", outputOrientation)
                .addField("positionDistance", positionDistance)
                .addField("positionOrientation", positionOrientation)
                .addField("cmdMotD", cmdRobot.getMoteur().getDroit())
                .addField("cmdMotG", cmdRobot.getMoteur().getGauche());

        monitoringWrapper.addTimeSeriePoint(serie);
    }
}
