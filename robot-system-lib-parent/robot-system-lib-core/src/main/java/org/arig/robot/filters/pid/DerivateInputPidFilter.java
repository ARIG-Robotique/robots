package org.arig.robot.filters.pid;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.filters.chain.ParallelChainFilter;
import org.arig.robot.filters.chain.SerialChainFilter;
import org.arig.robot.filters.common.DerivateFilter;
import org.arig.robot.filters.common.IntegralFilter;
import org.arig.robot.filters.common.IntegralLimitedFilter;
import org.arig.robot.filters.common.LimiterFilter;
import org.arig.robot.filters.common.ProportionalFilter;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class DerivateInputPidFilter extends AbstractPidFilter {

  private final IntegralFilter integral;
  private final DerivateFilter derivate;
  private final SerialChainFilter<Double> integralChain;
  private final SerialChainFilter<Double> derivateChain;
  private final ParallelChainFilter pi;

  public DerivateInputPidFilter(String name) {
    this(name, null);
  }

  public DerivateInputPidFilter(String name, Double integralLimit) {
    super(name);

    if (integralLimit != null) {
      integral = new IntegralLimitedFilter(0d, new LimiterFilter(0d, integralLimit, LimiterFilter.LimiterType.MIRROR));
    } else {
      integral = new IntegralFilter(0d);
    }
    derivate = new DerivateFilter(0d);

    integralChain = new SerialChainFilter<>();
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
  protected Map<String, Number> customMonitoringFields() {
    Map<String, Number> fields = new HashMap<>();
    fields.put("p", kp().lastResult());
    fields.put("i", integralChain.lastResult());
    fields.put("d", derivateChain.lastResult());
    return fields;
  }

  @Override
  public void reset() {
    super.reset();
    integral.reset();
    derivate.reset();
  }
}
