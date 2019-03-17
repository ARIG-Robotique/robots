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
public class TrapezoidalRampFilter extends AbstractRampFilter {

    private double posToDecel;
    private double currentVitesse;

    @Setter
    private boolean frein;

    /**
     * Instantiates a new ramp.
     *
     * @param name the filter name for monitoring
     */
    public TrapezoidalRampFilter(final String name) {
        this(name, RampType.LINEAR);
    }

    /**
     * Instantiates a new ramp.
     *
     * @param name the filter name for monitoring
     * @param type Le type de rampe
     */
    public TrapezoidalRampFilter(final String name, final RampType type) {
        this(name, type, 10, 100, 100);
    }

    /**
     * Instantiates a new ramp.
     *
     * @param name         the filter tag name for monitoring
     * @param sampleTimeMs the sample time in ms
     * @param rampAcc      the ramp acc
     * @param rampDec      the ramp dec
     */
    public TrapezoidalRampFilter(final String name, final RampType type, final double sampleTimeMs, final double rampAcc,
                                 final double rampDec) {
        super(name, type, sampleTimeMs, rampAcc, rampDec);
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
    }

    @Override
    protected Map<String, Number> specificMonitoringFields() {
        final Map<String, Number> fields = new HashMap<>();
        fields.put("distanceDeceleration", posToDecel);
        fields.put("currentVitesse", currentVitesse);
        fields.put("frein", frein ? 1 : 0);

        return fields;
    }

    /**
     * Application du filtre de rampe.
     */
    @Override
    public Long rampFilter(final Long input) {
        double targetVitesse = getType() == RampType.ANGULAR ? currentVitesse : getConsigneVitesse();

        // Calcul de la distance de décéleration en fonction des parametres
        posToDecel = conv.mmToPulse(Math.pow(targetVitesse, 2) / (2 * getRampDec()));

        if ((Math.abs(input) <= posToDecel && frein) || currentVitesse > getConsigneVitesse()) {
            currentVitesse -= getStepVitesseDecel();

            // Controle pour interdire les valeurs négatives
            currentVitesse = Math.max(currentVitesse, 0);

        } else if (currentVitesse < getConsigneVitesse()) {
            currentVitesse += getStepVitesseAccel();

            // Evite les oscilations en régime établie
            currentVitesse = Math.min(currentVitesse, getConsigneVitesse());
        }

        // Calcul de la valeur filtré de position en fonction de la vitesse calculé sur la rampe.
        double outPosition = conv.mmToPulse(currentVitesse) * getSampleTimeS();
        if (input < 0) {
            outPosition = -outPosition;
        }
        return Double.valueOf(outPosition).longValue();
    }
}