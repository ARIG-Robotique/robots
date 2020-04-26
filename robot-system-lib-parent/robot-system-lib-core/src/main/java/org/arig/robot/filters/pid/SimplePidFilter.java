package org.arig.robot.filters.pid;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.filters.chain.ParallelChainFilter;
import org.arig.robot.filters.chain.SerialChainFilter;
import org.arig.robot.filters.common.DerivateFilter;
import org.arig.robot.filters.common.IntegralFilter;

@Slf4j
public class SimplePidFilter extends AbstractPidFilter {

    private final IntegralFilter integral;
    private final DerivateFilter derivate;
    private final ParallelChainFilter pid;

    public SimplePidFilter(String name) {
        super(name);

        integral = new IntegralFilter(0d);
        derivate = new DerivateFilter(0d);

        final SerialChainFilter<Double> integralChain = new SerialChainFilter<>();
        integralChain.addFilter(integral);
        integralChain.addFilter(ki());
        integralChain.addFilter(integralTime());

        final SerialChainFilter<Double>  derivateChain = new SerialChainFilter<>();
        derivateChain.addFilter(derivate);
        derivateChain.addFilter(kd());
        derivateChain.addFilter(derivateTime());

        pid = new ParallelChainFilter();
        pid.addFilter(kp());
        pid.addFilter(integralChain);
        pid.addFilter(derivateChain);
    }

    @Override
    protected String pidImpl() {
        return "simple";
    }

    @Override
    protected Double filterImpl(final Double input) {
        return pid.filter(error());
    }

    public final Double getErrorSum() {
        return this.integral.getSum();
    }

    @Override
    public void reset() {
        super.reset();
        integral.reset();
        derivate.reset();
    }
}
