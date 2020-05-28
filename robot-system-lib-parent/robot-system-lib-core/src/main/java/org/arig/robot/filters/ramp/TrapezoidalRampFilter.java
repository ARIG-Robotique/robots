package org.arig.robot.filters.ramp;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * The Class TrapezoidalRampFilter.
 *
 * @author gdepuille
 */
@Slf4j
public class TrapezoidalRampFilter extends AbstractGainFactorRampFilter {

    private double posToDecel;
    private double currentVitesse;
    private double vitesseMax;

    @Setter
    private boolean frein;

    /**
     * Instantiates a new ramp.
     *
     * @param name the filter name for monitoring
     */
    public TrapezoidalRampFilter(final String name) {
        this(name, 10, 100, 100);
    }

    public TrapezoidalRampFilter(final String name, final double sampleTimeMs, final double rampAcc, final double rampDec) {
        this(name, sampleTimeMs, rampAcc, rampDec, 1);
    }

    public TrapezoidalRampFilter(final String name, final double sampleTimeMs, final double rampAcc, final double rampDec, final double gain) {
        super(name, sampleTimeMs, rampAcc, rampDec, gain);
    }

    @Override
    protected String rampImpl() {
        return "trapezoidale";
    }

    /**
     * Reset.
     */
    @Override
    public void reset() {
        super.reset();
        posToDecel = 0;
        currentVitesse = 0;
        vitesseMax = 0;
    }

    @Override
    protected Map<String, Number> gainFactorSpecificMonitoringFields() {
        final Map<String, Number> fields = new HashMap<>();
        fields.put("distanceDeceleration", posToDecel);
        fields.put("currentVitesse", currentVitesse);
        fields.put("vitesseMax", vitesseMax);
        fields.put("frein", frein ? 1 : 0);

        return fields;
    }

    /**
     * Application du filtre de rampe.
     */
    @Override
    public Long rampFilter(final Long input) {
        // Calcul de la distance de décéleration en fonction des parametres
        posToDecel = conv.mmToPulse(currentVitesse * currentVitesse / (2 * getRampDec()));

        if (input > 0 && currentVitesse >= 0) {
            // Distance a parcourir en avant
            if (input < getStepVitesseAccel()) {
                // Distance restante très proche
                currentVitesse = input;

            } else if (currentVitesse > getConsigneVitesse() || (input <= posToDecel && frein)) {
                // Trop vite
                currentVitesse -= getStepVitesseDecel();

            } else if (currentVitesse < getConsigneVitesse()) {
                // Pas assez vite
                currentVitesse += getStepVitesseAccel();

                // Evite les oscilations en régime établie
                currentVitesse = Math.min(currentVitesse, getConsigneVitesse());
            }

        } else if (input < 0 && currentVitesse > 0) {
            // Distance dépasser en avant
            currentVitesse -= getStepVitesseDecel();

        } else if (input < 0 && currentVitesse <= 0) {
            // Distance a parcourir en arrière
            if (input > -getStepVitesseAccel()) {
                // Distance restante très proche
                currentVitesse = input;

            } else if (currentVitesse < -getConsigneVitesse() || (input >= -posToDecel && frein)) {
                // Trop vite
                currentVitesse += getStepVitesseDecel();

            } else if (currentVitesse > -getConsigneVitesse()) {
                // Pas assez vite
                currentVitesse -= getStepVitesseAccel();

                // Evite les oscilations en régime établie
                currentVitesse = Math.max(currentVitesse, -getConsigneVitesse());
            }

        } else if (input > 0 && currentVitesse < 0) {
            // Distance dépasser en arrière
            currentVitesse += getStepVitesseDecel();

        } else {
            // Pas de distance
            currentVitesse = 0;
        }

        // Calcul de la valeur filtré de position en fonction de la vitesse calculé sur la rampe.
        return (long) (conv.mmToPulse(currentVitesse) * getSampleTimeS());
    }
}
