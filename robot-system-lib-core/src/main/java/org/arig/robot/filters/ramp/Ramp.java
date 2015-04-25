package org.arig.robot.filters.ramp;

import lombok.extern.slf4j.Slf4j;

import org.arig.robot.utils.ConvertionRobotUnit;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

/**
 * The Class Ramp.
 * 
 * @author mythril
 */
@Slf4j
public class Ramp implements IRampFilter {

    /** The conv. */
    @Autowired
    private ConvertionRobotUnit conv;

    /** The sample time. */
    private double sampleTimeS;

    /** The ramp acc. */
    private double rampAcc;

    /** The ramp dec. */
    private double rampDec;

    /** The step vitesse accel. */
    private double stepVitesseAccel;

    /** The step vitesse decel. */
    private double stepVitesseDecel;

    /** The vitesse courante. */
    private double vitesseCourante;

    /** The distance decel. */
    private double distanceDecel;

    /**
     * Instantiates a new quad ramp.
     */
    public Ramp() {
        this(10, 100, 100);
    }

    /**
     * Instantiates a new quad ramp.
     * 
     * @param sampleTimeMs
     *            the sample time in ms
     * @param rampAcc
     *            the ramp acc
     * @param rampDec
     *            the ramp dec
     */
    public Ramp(final double sampleTimeMs, final double rampAcc,
                final double rampDec) {
        this.sampleTimeS = sampleTimeMs / 1000;
        this.rampAcc = rampAcc;
        this.rampDec = rampDec;

        log.info("Initialisation par défaut (SampleTime : {} ; Rampe ACC : {} ; Rampe DEC : {}", sampleTimeS, rampAcc, rampDec);

        reset();
        updateStepVitesse();
    }

    /**
     * Sets the sample time ms.
     * 
     * @param value
     *            the new sample time ms
     */
    @Override
    public void setSampleTime(final double value) {
        sampleTimeS = value / 1000;
        updateStepVitesse();
    }

    @Override
    public void setSampleTime(double value, TimeUnit unit) {
        setSampleTime((double) unit.toMillis((long) value));
    }

    /**
     * Sets the ramp acc.
     * 
     * @param value
     *            the new ramp acc
     */
    @Override
    public void setRampAcc(final double value) {
        rampAcc = value;
        updateStepVitesse();
    }

    /**
     * Sets the ramp dec.
     * 
     * @param value
     *            the new ramp dec
     */
    @Override
    public void setRampDec(final double value) {
        rampDec = value;
        updateStepVitesse();
    }

    /**
     * Update step vitesse.
     */
    private void updateStepVitesse() {
        stepVitesseAccel = rampAcc * sampleTimeS;
        stepVitesseDecel = rampDec * sampleTimeS;
    }

    /**
     * Reset.
     */
    @Override
    public void reset() {
        log.info("Reset des paramètres");

        distanceDecel = 0;
        vitesseCourante = 0;
    }

    /**
     * Application du filtre. Cette méthode est appelé depuis la sub routine
     * d'asservissement
     * 
     * @param vitesse
     *            the vitesse
     * @param consigne
     *            the consigne
     * @param frein
     *            the frein
     * @return the double
     */
    @Override
    public double filter(final double vitesse, final double consigne,
            final double mesure, final boolean frein) {
        // Calcul de la distance de décéleration en fonction des parametres
        distanceDecel = conv.mmToPulse(vitesseCourante * vitesseCourante / (2 * rampDec));
        if (vitesseCourante > vitesse || (Math.abs(consigne) <= distanceDecel && frein)) {
            vitesseCourante -= stepVitesseDecel;
        } else if (vitesseCourante < vitesse) {
            vitesseCourante += stepVitesseAccel;
        }

        // Valeur max (evite les oscilations en régime établie)
        vitesseCourante = Math.min(vitesseCourante, vitesse);

        // Controle pour interdire les valeurs négatives
        vitesseCourante = Math.max(vitesseCourante, 0);

        // Calcul de la valeur théorique en fonction de la vitesse.
        final double pulseForVitesse = conv.mmToPulse(vitesseCourante) * sampleTimeS;

        // Consigne théorique en fonction de la vitesse
        double ecartTheorique = pulseForVitesse;
        if (consigne < 0) {
            ecartTheorique = -ecartTheorique;
        }

        return ecartTheorique;
    }
}
