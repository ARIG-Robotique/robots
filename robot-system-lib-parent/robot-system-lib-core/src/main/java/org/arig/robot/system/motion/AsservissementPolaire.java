package org.arig.robot.system.motion;

import org.arig.robot.filters.pid.IPidFilter;
import org.arig.robot.filters.ramp.TrapezoidalRampFilter;
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

        // Distance
        rampDistance.setConsigneVitesse(cmdRobot.getVitesse().getDistance());
        rampDistance.setFrein(cmdRobot.isFrein());
        final double positionDistance = rampDistance.filter(cmdRobot.getConsigne().getDistance());
        pidDistance.setConsigne(positionDistance);
        final double distance = pidDistance.filter(encoders.getDistance());

        // Orientation
        rampOrientation.setConsigneVitesse(cmdRobot.getVitesse().getOrientation());
        rampOrientation.setFrein(true);
        final double positionOrientation = rampOrientation.filter(cmdRobot.getConsigne().getOrientation());
        pidOrientation.setConsigne(positionOrientation);
        final double orientation = pidOrientation.filter(encoders.getOrientation());

        // Consigne moteurs
        double cmdMotDroit = distance + orientation;
        double cmdMotGauche = distance - orientation;
        cmdRobot.getMoteur().setDroit((int) cmdMotDroit);
        cmdRobot.getMoteur().setGauche((int) cmdMotGauche);
    }
}
