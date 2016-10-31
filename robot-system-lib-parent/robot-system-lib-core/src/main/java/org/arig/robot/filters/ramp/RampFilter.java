package org.arig.robot.filters.ramp;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.MonitorPoint;
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

    private double sampleTimeS;
    private double rampAcc;
    private double rampDec;
    private double stepVitesseAccel;
    private double stepVitesseDecel;

    @Getter
    private double distanceRestante;

    @Getter
    private double distanceDecel;

    @Getter
    private double vitesseCourante;

    @Getter
    private double vitesseDemande;

    @Getter
    private double output;

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
     * @param name         the filter name for monitoring
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
        distanceDecel = 0;
        vitesseCourante = 0;
    }

    /**
     * Application du filtre. Cette méthode est appelé depuis la sub routine
     * d'asservissement
     *
     * @param vitesseDemande   the vitesse
     * @param distanceRestante the consigne
     * @param frein            the frein
     *
     * @return the double
     */
    @Override
    public double filter(final double vitesseDemande, final double distanceRestante, final boolean frein) {

        this.vitesseDemande = vitesseDemande;
        this.distanceRestante = distanceRestante;
        this.frein = frein;

        // Calcul de la distance de décéleration en fonction des parametres
        distanceDecel = conv.mmToPulse(vitesseCourante * vitesseCourante / (2 * rampDec));
        if (vitesseCourante > vitesseDemande || (Math.abs(distanceRestante) <= distanceDecel && frein)) {
            vitesseCourante -= stepVitesseDecel;
        } else if (vitesseCourante < vitesseDemande) {
            vitesseCourante += stepVitesseAccel;
        }

        // Valeur max (evite les oscilations en régime établie)
        vitesseCourante = Math.min(vitesseCourante, vitesseDemande);

        // Controle pour interdire les valeurs négatives
        vitesseCourante = Math.max(vitesseCourante, 0);

        // Calcul de la valeur théorique en fonction de la vitesse.
        double ecartTheorique = conv.mmToPulse(vitesseCourante) * sampleTimeS;
        if (distanceRestante < 0) {
            ecartTheorique = -ecartTheorique;
        }
        output = ecartTheorique;
        sendMonitoring();
        return output;
    }

    protected void sendMonitoring() {
        // Construction du monitoring
        MonitorPoint serie = new MonitorPoint()
                .tableName(name)
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .addField("distanceDeceleration", getDistanceDecel())
                .addField("distanceRestante", getDistanceRestante())
                .addField("vitesseCourante", getVitesseCourante())
                .addField("vitesseDemande", getVitesseDemande())
                .addField("frein", isFrein())
                .addField("output", getOutput());

        monitoringWrapper.addPoint(serie);
    }
}
