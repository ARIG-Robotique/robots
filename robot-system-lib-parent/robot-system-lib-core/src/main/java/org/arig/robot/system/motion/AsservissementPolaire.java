package org.arig.robot.system.motion;

import org.arig.robot.filters.pid.IPidFilter;
import org.arig.robot.filters.ramp.IRampFilter;
import org.arig.robot.model.CommandeRobot;
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
    @Qualifier("rampDistance")
    private IRampFilter rampDistance;

    @Autowired
    @Qualifier("rampOrientation")
    private IRampFilter rampOrientation;

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
        final double positionDistance = rampDistance.filter(cmdRobot.getVitesse().getDistance(), cmdRobot.getConsigne().getDistance(), encoders.getDistance(), cmdRobot.isFrein());
        final double distance = pidDistance.compute(positionDistance, encoders.getDistance());

        final double positionOrientation = rampOrientation.filter(cmdRobot.getVitesse().getOrientation(), cmdRobot.getConsigne().getOrientation(), encoders.getOrientation(), true);
        final double orientation = pidOrientation.compute(positionOrientation, encoders.getOrientation());

        // Consigne moteurs
        double cmdMotDroit = distance + orientation;
        double cmdMotGauche = distance - orientation;
        cmdRobot.getMoteur().setDroit((int) cmdMotDroit);
        cmdRobot.getMoteur().setGauche((int) cmdMotGauche);
    }
}
