package org.arig.robot.filters.pid;

import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.filters.common.ProportionalFilter;
import org.arig.robot.model.monitor.MonitorTimeSerie;
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.Map;

@Slf4j
public abstract class AbstractPidFilter implements IPidFilter {

    @Autowired
    private IMonitoringWrapper monitoringWrapper;

    @Getter
    private final String name;

    @Getter(AccessLevel.PROTECTED)
    @Accessors(fluent = true)
    private Double consigne;

    @Getter(AccessLevel.PROTECTED)
    @Accessors(fluent = true)
    private final ProportionalFilter kp;

    @Getter(AccessLevel.PROTECTED)
    @Accessors(fluent = true)
    private final ProportionalFilter ki;

    @Getter(AccessLevel.PROTECTED)
    @Accessors(fluent = true)
    private final ProportionalFilter kd;

    @Setter(AccessLevel.PROTECTED)
    @Getter(AccessLevel.PROTECTED)
    @Accessors(fluent = true)
    private Double input;

    @Setter(AccessLevel.PROTECTED)
    @Getter(AccessLevel.PROTECTED)
    @Accessors(fluent = true)
    private Double output;

    @Setter(AccessLevel.PROTECTED)
    @Getter(AccessLevel.PROTECTED)
    @Accessors(fluent = true)
    private Double error;

    protected abstract String pidImpl();
    protected abstract Double filterImpl(Double input);

    AbstractPidFilter(final String name) {
        this.name = name;

        kp = new ProportionalFilter(0d);
        ki = new ProportionalFilter(0d);
        kd = new ProportionalFilter(0d);
    }

    public void consigne(Double consigne) {
        this.consigne = consigne;
    }

    @Override
    public void setTunings(final double kp, final double ki, final double kd) {
        log.info("Configuration des paramètres PID {} ( Kp = {} ; Ki = {} ; Kd = {} )", getName(), kp, ki, kd);

        this.kp.setGain(kp);
        this.ki.setGain(ki);
        this.kd.setGain(kd);
    }

    @Override
    public Map<String, Double> getTunings() {
        return ImmutableMap.of(
                "kp", kp.getGain(),
                "ki", ki.getGain(),
                "kd", kd.getGain()
        );
    }

    @Override
    public void reset() {
        input = 0d;
        output = 0d;
    }

    @Override
    public final Double filter(Double input) {
        Assert.notNull(input, FILTER_VALUE_NULL_MESSAGE);

        this.input = input;
        error = consigne - input;
        output = filterImpl(input);

        // Construction du monitoring
        MonitorTimeSerie serie = new MonitorTimeSerie()
                .measurementName("correcteur_pid")
                .addTag(MonitorTimeSerie.TAG_NAME, name)
                .addTag(MonitorTimeSerie.TAG_IMPLEMENTATION, pidImpl())
                .addField("kp", kp().getGain())
                .addField("ki", ki().getGain())
                .addField("kd", kd().getGain())
                .addField("consigne", consigne)
                .addField("input", input)
                .addField("output", output)
                .addField("error", error)
                .addField("errorSum", getErrorSum());

        monitoringWrapper.addTimeSeriePoint(serie);

        return output();
    }
}
