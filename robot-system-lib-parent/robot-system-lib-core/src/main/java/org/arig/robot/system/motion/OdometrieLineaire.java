package org.arig.robot.system.motion;

import org.arig.robot.model.enums.TypeOdometrie;
import org.arig.robot.system.encoders.Abstract2WheelsEncoders;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The Class Odometrie.
 *
 * @author gdepuille
 */
public class OdometrieLineaire extends AbstractOdometrie {

    @Autowired
    private Abstract2WheelsEncoders encoders;

    @Autowired
    private ConvertionRobotUnit conv;

    public OdometrieLineaire() {
        super(TypeOdometrie.LINEAIRE);
    }

    /**
     * Ce calcul est effectué avec le postulat que durant le laps de temps ecoulé le robot a roulé droit (pas en
     * courbe). On parle donc d'approximation linéaire.
     */
    @Override
    protected void process() {
        double newTheta = getPosition().getAngle() + encoders.getOrientation();

        // Ajustement a PI près
        if (newTheta > conv.getPiPulse()) {
            newTheta -= conv.getPi2Pulse();
        } else if (newTheta < -conv.getPiPulse()) {
            newTheta += conv.getPi2Pulse();
        }

        // Calcul du déplacement sur X et Y en fonction de l'angle et de la distance
        final double thetaRad = conv.pulseToRad(newTheta);
        final double dX = encoders.getDistance() * Math.cos(thetaRad);
        final double dY = encoders.getDistance() * Math.sin(thetaRad);

        // Sauvegarde nouvelle position (en pulse)
        getPosition().setAngle(newTheta);
        getPosition().addDeltaX(dX);
        getPosition().addDeltaY(dY);
    }
}
