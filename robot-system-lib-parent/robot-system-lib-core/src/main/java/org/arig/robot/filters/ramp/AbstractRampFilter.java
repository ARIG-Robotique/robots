package org.arig.robot.filters.ramp;

import com.google.common.collect.ImmutableMap;
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

    @Setter
    @Getter(AccessLevel.PROTECTED)
    private double consigneVitesse;

    @Setter
    private boolean bypass = false;

    /**
     * Instantiates a new ramp.
     *
     * @param name         the filter tag name for monitoring
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

    @Override
    public void reset() {
        input = 0;
        output = 0;
    }

    public void setSampleTimeMs(final double value) {
        sampleTimeS = value / 1000;
        updateStepVitesse();
    }

    public void setSampleTime(double value, TimeUnit unit) {
        setSampleTimeMs((double) unit.toMillis((long) value));
    }

    @Override
    public void setRamps(double rampAcc, double rampDec) {
        log.info("Configuration des rampes {} ( acc = {} ; dec = {} )", getName(), rampAcc, rampDec);

        this.rampAcc = rampAcc;
        this.rampDec = rampDec;
        updateStepVitesse();
    }

    @Override
    public Map<String, Double> getRamps() {
        return ImmutableMap.of(
                "rampAcc", rampAcc,
                "rampDec", rampDec
        );
    }

    @Override
    public final Long filter(Long input) {
        Assert.notNull(input, FILTER_VALUE_NULL_MESSAGE);

        this.input = input;
        this.output = rampFilter(input, bypass);
        sendMonitoring();
        return this.output;
    }

    @Override
    public Long lastResult() {
        return output;
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

    protected abstract String rampImpl();

    protected abstract Long rampFilter(Long input, boolean bypass);

    protected abstract Map<String, Number> specificMonitoringFields();

    private void updateStepVitesse() {
        stepVitesseAccel = rampAcc * sampleTimeS;
        stepVitesseDecel = rampDec * sampleTimeS;
    }
}
