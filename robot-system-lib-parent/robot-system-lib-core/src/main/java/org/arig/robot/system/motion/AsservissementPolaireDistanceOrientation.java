package org.arig.robot.system.motion;

import org.arig.robot.filters.common.LimiterFilter;
import org.arig.robot.filters.pid.PidFilter;
import org.arig.robot.filters.ramp.TrapezoidalRampFilter;
import org.arig.robot.model.CommandeRobot;
import org.arig.robot.model.enums.TypeConsigne;
import org.arig.robot.system.encoders.Abstract2WheelsEncoders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * The Class AsservissementPolaireDistanceOrientation.
 *
 * @author gdepuille
 */
public class AsservissementPolaireDistanceOrientation implements IAsservissementPolaire {

    @Autowired
    private CommandeRobot cmdRobot;

    @Autowired
    private Abstract2WheelsEncoders encoders;

    @Autowired
    @Qualifier("pidDistance")
    private PidFilter pidDistance;

    @Autowired
    @Qualifier("pidOrientation")
    private PidFilter pidOrientation;

    @Autowired
    @Qualifier("rampDistance")
    private TrapezoidalRampFilter rampDistance;

    @Autowired
    @Qualifier("rampOrientation")
    private TrapezoidalRampFilter rampOrientation;

    private final LimiterFilter limiterMoteurGauche;
    private final LimiterFilter limiterMoteurDroit;

    /**
     * Instantiates a new asservissement polaire.
     */
    public AsservissementPolaireDistanceOrientation() {
        this(new LimiterFilter(-Double.MAX_VALUE + 1, Double.MAX_VALUE), new LimiterFilter(-Double.MAX_VALUE + 1, Double.MAX_VALUE));
    }

    public AsservissementPolaireDistanceOrientation(LimiterFilter limiterMoteurGauche, LimiterFilter limiterMoteurDroit) {
        super();
        this.limiterMoteurGauche = limiterMoteurGauche;
        this.limiterMoteurDroit = limiterMoteurDroit;
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
    public void process(final long timeStepMs, boolean obstacleDetected) {
        final double distance;
        final double orientation;

        // Distance
        if (cmdRobot.isType(TypeConsigne.DIST) || cmdRobot.isType(TypeConsigne.XY)) {
            rampDistance.setConsigneVitesse(cmdRobot.getVitesse().getDistance());
            rampDistance.setFrein(cmdRobot.isFrein());
            rampDistance.setBypass(cmdRobot.isBypassRampDistance() || obstacleDetected);
            final double positionDistance = rampDistance.filter(cmdRobot.getConsigne().getDistance());
            pidDistance.setSampleTimeMs(timeStepMs);
            pidDistance.consigne(positionDistance);
            distance = pidDistance.filter(encoders.getDistance());
        } else {
            distance = 0;
        }

        // Orientation
        if (cmdRobot.isType(TypeConsigne.ANGLE) || cmdRobot.isType(TypeConsigne.XY)) {
            rampOrientation.setConsigneVitesse(cmdRobot.getVitesse().getOrientation());
            rampOrientation.setFrein(true);
            rampOrientation.setBypass(cmdRobot.isBypassRampOrientation() || obstacleDetected);
            final double positionOrientation = rampOrientation.filter(cmdRobot.getConsigne().getOrientation());
            pidOrientation.setSampleTimeMs(timeStepMs);
            pidOrientation.consigne(positionOrientation);
            orientation = pidOrientation.filter(encoders.getOrientation());
        } else {
            orientation = 0;
        }

        // Consigne moteurs
        double cmdMotGauche = distance - orientation;
        double cmdMotDroit = distance + orientation;

        cmdRobot.getMoteur().setGauche(limiterMoteurGauche.filter(cmdMotGauche).intValue());
        cmdRobot.getMoteur().setDroit(limiterMoteurDroit.filter(cmdMotDroit).intValue());
    }
}
