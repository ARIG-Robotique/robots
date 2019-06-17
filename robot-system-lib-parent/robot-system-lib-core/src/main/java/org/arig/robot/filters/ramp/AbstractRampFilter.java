package org.arig.robot.filters.ramp;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.monitor.MonitorTimeSerie;
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public abstract class AbstractRampFilter implements IRampFilter {

    @Autowired
    protected ConvertionRobotUnit conv;

    @Autowired
    protected IMonitoringWrapper monitoringWrapper;

    @Getter
    private String name;

    @Getter
    private double sampleTimeS;

    private long input, output;

    @Getter(AccessLevel.PROTECTED)
    private double rampAcc, rampDec, stepVitesseAccel, stepVitesseDecel;

    @Getter
    @Setter
    private double consigneVitesse;

    /**
     * Instantiates a new ramp.
     *
     * @param name      the filter tag name for monitoring
     * @param sampleTimeMs the sample time in ms
     * @param rampAcc      the ramp acc
     * @param rampDec      the ramp dec
     */
    protected AbstractRampFilter(final String name, final double sampleTimeMs,
                                 final double rampAcc, final double rampDec) {
        this.name = name;
        this.sampleTimeS = sampleTimeMs / 1000;
        this.rampAcc = rampAcc;
        this.rampDec = rampDec;

        log.info("Initialisation par défaut (SampleTime : {} ; Rampe ACC : {} ; Rampe DEC : {}", sampleTimeS, rampAcc, rampDec);

        reset();
        updateStepVitesse();
    }

    protected abstract String rampImpl();
    protected abstract Long rampFilter(Long input);

    @Override
    public void reset() {
        input = 0;
        output = 0;
    }

    protected abstract Map<String, Number> specificMonitoringFields();

    /**
     * Sets the sample time ms.
     *
     * @param value the new sample time ms
     */
    public void setSampleTime(final double value) {
        sampleTimeS = value / 1000;
        updateStepVitesse();
    }

    public void setSampleTime(double value, TimeUnit unit) {
        setSampleTime((double) unit.toMillis((long) value));
    }

    /**
     * Sets the ramp acc.
     *
     * @param value the new ramp acc
     */
    public void setRampAcc(final double value) {
        rampAcc = value;
        updateStepVitesse();
    }

    /**
     * Sets the ramp dec.
     *
     * @param value the new ramp dec
     */
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

    @Override
    public final Long filter(Long input) {
        Assert.notNull(input, FILTER_VALUE_NULL_MESSAGE);

        this.input = input;
        this.output = rampFilter(input);
        sendMonitoring();
        return this.output;
    }

    protected void sendMonitoring() {
        // Construction du monitoring
        MonitorTimeSerie serie = new MonitorTimeSerie()
                .measurementName("ramp_vitesse")
                .addTag(MonitorTimeSerie.TAG_NAME, name)
                .addTag(MonitorTimeSerie.TAG_IMPLEMENTATION, rampImpl())
                .addField("input", input)
                .addField("output", output)
                .addField("consigne", consigneVitesse);

        specificMonitoringFields().forEach(serie::addField);

        monitoringWrapper.addTimeSeriePoint(serie);
    }
}
