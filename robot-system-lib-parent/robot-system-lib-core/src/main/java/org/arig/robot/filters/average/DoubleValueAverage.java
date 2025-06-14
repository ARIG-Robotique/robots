package org.arig.robot.filters.average;

import java.util.function.BinaryOperator;

/**
 * @author gdepuille on 14/05/17.
 */
public class DoubleValueAverage extends AbstractCircularFifoValueAverage<Double> {

  public DoubleValueAverage() {
    this(3);
  }

  public DoubleValueAverage(int limit) {
    super(limit);
  }

  @Override
  protected Double identityValue() {
    return 0d;
  }

  @Override
  protected BinaryOperator<Double> reduceFunction() {
    return Double::sum;
  }

  @Override
  protected Double effectiveAverage(Double reducedValue, int queueSize) {
    return reducedValue / queueSize;
  }
}
