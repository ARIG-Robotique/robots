package org.arig.robot.filters.common;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.arig.robot.filters.Filter;
import org.springframework.util.Assert;

public class DerivateFilter implements Filter<Double, Double> {

  public static final String INITIAL_VALUE_NULL_MESSAGE = "La valeur initial ne peut être null";

  private final Double initial;

  @Getter
  private Double lastValue;

  @Getter
  @Accessors(fluent = true)
  private Double lastResult;

  public DerivateFilter(Double initial) {
    Assert.notNull(initial, INITIAL_VALUE_NULL_MESSAGE);
    this.initial = initial;
    this.lastValue = initial;
  }

  @Override
  public void reset() {
    lastValue = initial;
  }

  @Override
  public Double filter(Double value) {
    Assert.notNull(value, FILTER_VALUE_NULL_MESSAGE);
    lastResult = value - lastValue;
    lastValue = value;
    return lastResult;
  }
}
