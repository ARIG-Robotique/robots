package org.arig.robot.filters.pid;

import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.filters.chain.ParallelChainFilter;
import org.arig.robot.filters.chain.SerialChainFilter;
import org.arig.robot.filters.common.DerivateFilter;
import org.arig.robot.filters.common.IntegralFilter;
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

    @Setter
    @Getter(AccessLevel.PROTECTED)
    private double consigne, input, output;

    final ProportionalFilter propP, propI, propD;
    private final IntegralFilter integral;
    private final DerivateFilter derivate;
    //private final LimiterFilter limiter;

    private final SerialChainFilter<Double> integralChain, derivateChain, completePid;
    private final ParallelChainFilter pid;

    AbstractPidFilter(final String name, double min, double max) {
        this.name = name;
        propP = new ProportionalFilter(0d);
        propI = new ProportionalFilter(0d);
        propD = new ProportionalFilter(0d);
        integral = new IntegralFilter(0d);
        derivate = new DerivateFilter(0d);
        //limiter = new LimiterFilter(min, max);

        integralChain = new SerialChainFilter<>();
        integralChain.addFilter(integral);
        integralChain.addFilter(propI);
        //integralChain.addFilter(limiter);

        derivateChain = new SerialChainFilter<>();
        derivateChain.addFilter(derivate);
        derivateChain.addFilter(propD);

        pid = new ParallelChainFilter();
        pid.addFilter(propP);
        pid.addFilter(integralChain);
        pid.addFilter(derivateChain);

        completePid = new SerialChainFilter<>();
        completePid.addFilter(pid);
        //completePid.addFilter(limiter);
    }

    protected abstract String pidImpl();

    @Override
    public void setTunings(final double kp, final double ki, final double kd) {
        log.info("Configuration des paramètres PID {} ( Kp = {} ; Ki = {} ; Kd = {} )", getName(), kp, ki, kd);

        propP.setGain(kp);
        propI.setGain(ki);
        propD.setGain(kd);
    }

    @Override
    public Map<String, Double> getTunings() {
        return ImmutableMap.of(
                "kp", propP.getGain(),
                "ki", propI.getGain(),
                "kd", propD.getGain()
        );
    }

    @Override
    public void reset() {
        integral.reset();
        derivate.reset();

        input = 0;
        output = 0;
    }

    @Override
    public final Double filter(Double input) {
        Assert.notNull(input, FILTER_VALUE_NULL_MESSAGE);

        this.input = input;
        double error = consigne - input;
        this.output = completePid.filter(error);

        // Construction du monitoring
        MonitorTimeSerie serie = new MonitorTimeSerie()
                .measurementName("correcteur_pid")
                .addTag(MonitorTimeSerie.TAG_NAME, name)
                .addTag(MonitorTimeSerie.TAG_IMPLEMENTATION, pidImpl())
                .addField("kp", propP.getGain())
                .addField("ki", propI.getGain())
                .addField("kd", propD.getGain())
                .addField("consigne", getConsigne())
                .addField("input", getInput())
                .addField("output", getOutput())
                .addField("error", error)
                .addField("errorSum", integral.getSum());

        monitoringWrapper.addTimeSeriePoint(serie);

        return this.output;
    }

    public final Double getPidErrorSum() {
        return this.integral.getSum();
    }
}
