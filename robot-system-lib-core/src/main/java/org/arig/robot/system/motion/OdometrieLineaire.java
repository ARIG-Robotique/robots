package org.arig.robot.system.motion;

import org.arig.robot.system.encoders.Abstract2WheelsEncoders;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.arig.robot.vo.enums.TypeOdometrie;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The Class Odometrie.
 * 
 * @author mythril
 */
public class OdometrieLineaire extends AbstractOdometrie {

    /** The encoders. */
    @Autowired
    private Abstract2WheelsEncoders encoders;

    /** The conv. */
    @Autowired
    private ConvertionRobotUnit conv;

    /**
     * Instantiates a new linear odometrie.
     */
    public OdometrieLineaire() {
        super(TypeOdometrie.LINEAIRE);
    }

    /**
     * Ce calcul est effectué avec le postulat que durant le labs de temps écoulé le robot a roulé droit (pas en
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
