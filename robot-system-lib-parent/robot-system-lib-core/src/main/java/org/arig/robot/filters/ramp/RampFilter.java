package org.arig.robot.filters.ramp;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.monitor.MonitorTimeSerie;
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

/**
 * The Class RampFilter.
 *
 * @author gdepuille
 */
@Slf4j
public class RampFilter implements IRampFilter {

    @Autowired
    private ConvertionRobotUnit conv;

    @Autowired
    private IMonitoringWrapper monitoringWrapper;

    private String name;

    @Getter
    private double sampleTimeS;

    private double rampAcc;
    private double rampDec;
    private double stepVitesseAccel;
    private double stepVitesseDecel;

    @Getter
    private double input;

    @Getter
    private double output;

    @Getter
    private double posToDecel;

    @Getter
    private double inputVitesse;

    @Getter
    private double currentVitesse;

    @Getter
    private boolean frein;

    /**
     * Instantiates a new ramp.
     *
     * @param name the filter name for monitoring
     */
    public RampFilter(final String name) {
        this(name, 10, 100, 100);
    }

    /**
     * Instantiates a new ramp.
     *
     * @param name      the filter tag name for monitoring
     * @param sampleTimeMs the sample time in ms
     * @param rampAcc      the ramp acc
     * @param rampDec      the ramp dec
     */
    public RampFilter(final String name, final double sampleTimeMs, final double rampAcc,
                      final double rampDec) {
        this.name = name;
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
     * @param value the new sample time ms
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
     * @param value the new ramp acc
     */
    @Override
    public void setRampAcc(final double value) {
        rampAcc = value;
        updateStepVitesse();
    }

    /**
     * Sets the ramp dec.
     *
     * @param value the new ramp dec
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
        input = 0;
        output = 0;
        posToDecel = 0;
        currentVitesse = 0;
    }

    /**
     * Application du filtre. Cette méthode est appelé depuis la sub routine
     * d'asservissement
     *
     * @param inputVitesse the vitesse
     * @param inputPos     the consigne
     * @param frein        the frein
     * @return the double
     */
    @Override
    public double filter(final double inputVitesse, final double inputPos, final double distanceReelle, final boolean frein) {

        double currentVitesseReele = conv.pulseToMm(distanceReelle / sampleTimeS);

        this.inputVitesse = inputVitesse;
        this.input = inputPos;
        this.frein = frein;

        double targetVitesse = name.contains("orientation") ? currentVitesse : inputVitesse;

        // Calcul de la distance de décéleration en fonction des parametres
        posToDecel = conv.mmToPulse(Math.pow(targetVitesse, 2) / (2 * rampDec));

        if ((Math.abs(input) <= posToDecel && frein) || currentVitesse > inputVitesse) {
            currentVitesse -= stepVitesseDecel;
        } else if (currentVitesse < inputVitesse) {
            currentVitesse += stepVitesseAccel;
        }

        // Valeur max (evite les oscilations en régime établie)
        currentVitesse = Math.min(currentVitesse, inputVitesse);

        // Controle pour interdire les valeurs négatives
        currentVitesse = Math.max(currentVitesse, 0);

        // Calcul de la valeur filtré de position en fonction de la vitesse calculé sur la rampe.
        double outPosition = conv.mmToPulse(currentVitesse) * sampleTimeS;
        if (inputPos < 0) {
            outPosition = -outPosition;
        }
        output = outPosition;
        sendMonitoring();
        return output;

    }

    private void sendMonitoring() {
        // Construction du monitoring
        MonitorTimeSerie serie = new MonitorTimeSerie()
                .measurementName("ramp_vitesse")
                .addTag(MonitorTimeSerie.TAG_NAME, name)
                .addField("distanceDeceleration", getPosToDecel())
                .addField("mesure", getInput())
                .addField("currentVitesse", getCurrentVitesse())
                .addField("inputVitesse", getInputVitesse())
                .addField("frein", isFrein() ? 1 : 0)
                .addField("output", getOutput());

        monitoringWrapper.addTimeSeriePoint(serie);
    }
}
