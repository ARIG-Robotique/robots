package org.arig.robot.system.motion;

import org.arig.robot.filters.pid.IPidFilter;
import org.arig.robot.filters.ramp.IRampFilter;
import org.arig.robot.model.CommandeRobot;
import org.arig.robot.model.MonitorPoint;
import org.arig.robot.model.enums.TypeConsigne;
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.arig.robot.system.encoders.Abstract2WheelsEncoders;
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
    private double vitesseDistance;
    private double vitesseOrientation;

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
            vitesseDistance = rampDistance.filter(cmdRobot.getVitesse().getDistance(), cmdRobot.getConsigne().getDistance(), cmdRobot.isFrein());
            //outputDistance = pidDistance.compute(vitesseDistance, encoders.getDistance());
        } else {
            outputDistance = vitesseDistance = 0;
        }
        // Génération consigne pour l'orientation
        if (cmdRobot.isType(TypeConsigne.ANGLE) || cmdRobot.isType(TypeConsigne.XY)) {
            vitesseOrientation = rampOrientation.filter(cmdRobot.getVitesse().getOrientation(), cmdRobot.getConsigne().getOrientation(), true);
            //outputOrientation = pidOrientation.compute(vitesseOrientation, encoders.getOrientation());
        } else {
            outputOrientation = vitesseOrientation = 0;
        }

        // Consigne moteurs
        double cmdMotDroit = pidMoteurDroit.compute(vitesseDistance + vitesseOrientation, encoders.getDroit());
        double cmdMotGauche = pidMoteurGauche.compute(vitesseDistance - vitesseOrientation, encoders.getGauche());
        //double cmdMotDroit = outputDistance + outputOrientation;
        //double cmdMotGauche = outputDistance - outputOrientation;
        cmdRobot.getMoteur().setDroit((int) cmdMotDroit);
        cmdRobot.getMoteur().setGauche((int) cmdMotGauche);

        sendMonitoring();
    }

    private void sendMonitoring() {
        // Construction du monitoring
        MonitorPoint serie = new MonitorPoint()
                .tableName("asserv_polaire")
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .addField("outputDistance", outputDistance)
                .addField("outputOrientation", outputOrientation)
                .addField("vitesseDistance", vitesseDistance)
                .addField("vitesseOrientation", vitesseOrientation)
                .addField("cmdMotD", cmdRobot.getMoteur().getDroit())
                .addField("cmdMotG", cmdRobot.getMoteur().getGauche());

        monitoringWrapper.addPoint(serie);
    }
}
