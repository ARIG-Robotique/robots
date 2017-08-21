package org.arig.robot.filters.pid;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.arig.robot.filters.chain.ParallelChainFilter;
import org.arig.robot.filters.chain.SerialChainFilter;
import org.arig.robot.filters.common.DerivateFilter;
import org.arig.robot.filters.common.IntegralFilter;
import org.arig.robot.filters.common.ProportionalFilter;
import org.arig.robot.model.monitor.MonitorTimeSerie;
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

/**
 * @author gdepuille on 12/10/16.
 */
public abstract class AbstractPidFilter implements IPidFilter {

    @Autowired
    private IMonitoringWrapper monitoringWrapper;

    private final String name;

    @Setter
    @Getter(AccessLevel.PROTECTED)
    private double consigne, input, output;

    final ProportionalFilter propP, propI, propD;
    private final IntegralFilter integral;
    private final DerivateFilter derivate;

    private final SerialChainFilter<Double> integralChain, derivateChain;
    private final ParallelChainFilter pid;

    AbstractPidFilter(final String name) {
        this.name = name;
        propP = new ProportionalFilter(0d);
        propI = new ProportionalFilter(0d);
        propD = new ProportionalFilter(0d);
        integral = new IntegralFilter(0d);
        derivate = new DerivateFilter(0d);

        integralChain = new SerialChainFilter<>();
        integralChain.addFilter(integral);
        integralChain.addFilter(propI);

        derivateChain = new SerialChainFilter<>();
        derivateChain.addFilter(derivate);
        derivateChain.addFilter(propD);

        pid = new ParallelChainFilter();
        pid.addFilter(propP);
        pid.addFilter(integralChain);
        pid.addFilter(derivateChain);
    }

    protected abstract String pidImpl();

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
        this.output = pid.filter(error);

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
}
