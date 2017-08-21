package org.arig.robot.filters.ramp.experimental;

import org.arig.robot.filters.common.DerivateFilter;
import org.arig.robot.filters.common.DifferenceFilter;
import org.arig.robot.filters.common.IntegralFilter;
import org.arig.robot.filters.ramp.AbstractRampFilter;

import java.util.Collections;
import java.util.Map;

/**
 * Implementation de Ramp avec limite de la dérivé seconde (acceleration) et de la dérivé première (vitesse).
 *
 * Cette implémentation est experimental, utilisation a vos risque et peril
 */
public class ExperimentalRampFilter extends AbstractRampFilter {

    private IntegralFilter iSpeed = new IntegralFilter(0d);
    private IntegralFilter iPos = new IntegralFilter(0d);
    private DerivateFilter dAccel = new DerivateFilter(0d);
    private DerivateFilter dSpeed = new DerivateFilter(0d);

    public ExperimentalRampFilter(final String name) {
        this(name, 10, 100, 100);
    }

    public ExperimentalRampFilter(String name, double sampleTimeMs, double rampAcc, double rampDec) {
        super(name, RampType.LINEAR, sampleTimeMs, rampAcc, rampDec);
    }

    @Override
    protected String rampImpl() {
        return "quad";
    }

    @Override
    protected Map<String, Number> specificMonitoringFields() {
        return Collections.emptyMap();
    }

    /**
     * Reset.
     */
    @Override
    public void reset() {
        super.reset();
        if (iSpeed != null) {
            iSpeed.reset();
        }
        if (iPos != null) {
            iPos.reset();
        }
        if (dAccel != null) {
            dAccel.reset();
        }
        if (dSpeed != null) {
            dSpeed.reset();
        }
    }

    /**
     * Application du filtrage de rampe
     *
     * @param input Distance restante a parcourir
     * @return Valeur de position en fonction de l'acceleration, vitesse et distance restante
     */
    @Override
    protected Long rampFilter(Long input) {
        // Définition de la référence sur la position restante (input)
        DifferenceFilter diff = new DifferenceFilter(input.doubleValue());

        // Variable de fonctionnement
        double position = iPos.filter(0d);
        double currentSpeed = dSpeed.filter(position);
        double speed = 0d;

        double deltaPosition = diff.filter(position);
        if (deltaPosition > 0) {
            // On n'est avant le point a atteindre
            speed = speedCommand(deltaPosition);
            speed = iSpeed.filter(speedError(speed, currentSpeed));

        } else if (deltaPosition < 0) {
            // On a dépassé le point a atteindre
            speed = -speedCommand(-deltaPosition);
            speed = iSpeed.filter(speedError(-speed, -currentSpeed));

        } else {
            // On est au point
            iSpeed.reset();
        }

        if ((getStepVitesseDecel() < deltaPosition && deltaPosition < -getStepVitesseDecel())
                || (-Math.abs(speed) < deltaPosition && deltaPosition < Math.abs(speed))) {
            // On est proche du point a atteindre
            speed = deltaPosition;
        }

        position = iPos.filter(speed);
        return Double.valueOf(conv.mmToPulse(position) * getSampleTimeS()).longValue();
    }

    private double speedCommand(double deltaPosition) {
        double a = Math.sqrt(getStepVitesseDecel() * deltaPosition * 2);
        double b = Math.min(getStepVitesseDecel(), getStepVitesseDecel() * deltaPosition) / 2;

        return Math.min(a - b , getConsigneVitesse());
    }

    private double speedError(double speed, double currentSpeed) {
        if (currentSpeed < speed) {
            // Pas assez vite
            return Math.min(getStepVitesseAccel(), speed - currentSpeed);
        } else if (currentSpeed > speed) {
            // Top vite !!
            return Math.max(-getStepVitesseDecel(), speed - currentSpeed);
        }

        // Cool !!
        return 0;
    }
}
