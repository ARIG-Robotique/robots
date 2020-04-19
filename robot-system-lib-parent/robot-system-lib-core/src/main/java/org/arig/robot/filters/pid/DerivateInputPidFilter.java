package org.arig.robot.filters.pid;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.filters.chain.ParallelChainFilter;
import org.arig.robot.filters.chain.SerialChainFilter;
import org.arig.robot.filters.common.DerivateFilter;
import org.arig.robot.filters.common.IntegralFilter;
import org.arig.robot.filters.common.ProportionalFilter;

@Slf4j
public class DerivateInputPidFilter extends AbstractPidFilter {

    private final IntegralFilter integral;
    private final DerivateFilter derivate;
    private final SerialChainFilter<Double> derivateChain;
    private final ParallelChainFilter pi;

    public DerivateInputPidFilter(String name) {
        super(name);

        integral = new IntegralFilter(0d);
        derivate = new DerivateFilter(0d);

        final SerialChainFilter<Double> integralChain = new SerialChainFilter<>();
        integralChain.addFilter(integral);
        integralChain.addFilter(ki());

        pi = new ParallelChainFilter();
        pi.addFilter(kp());
        pi.addFilter(integralChain);

        derivateChain = new SerialChainFilter<>();
        derivateChain.addFilter(derivate);
        derivateChain.addFilter(kd());
        derivateChain.addFilter(new ProportionalFilter(-1d));
    }

    @Override
    protected String pidImpl() {
        return "derivate-input";
    }

    @Override
    protected Double filterImpl(final Double input) {
        return pi.filter(error()) + derivateChain.filter(input);
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
