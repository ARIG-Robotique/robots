package org.arig.robot.filters.sensors;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.arig.robot.filters.Filter;
import org.arig.robot.filters.median.DoubleMedianFilter;
import org.arig.robot.filters.range.AroundValueDoubleFilter;

public class GP2DPhantomFilter implements Filter<Double, Double> {

  private final DoubleMedianFilter medianFilter;
  private final AroundValueDoubleFilter aroundValueFilter;

  @Getter
  @Accessors(fluent = true)
  private Double lastResult = 0.0;

  public GP2DPhantomFilter(int medianValuesSize, double aroundValue) {
    this.medianFilter = new DoubleMedianFilter(medianValuesSize);
    this.aroundValueFilter = new AroundValueDoubleFilter(aroundValue);
  }

  @Override
  public Double filter(Double value) {
    double medianValue = medianFilter.filter(value);
    aroundValueFilter.base(medianValue);
    if (!aroundValueFilter.filter(value)) {
      lastResult = value;
    } else {
      medianFilter.removeLastValue();
    }

    return lastResult;
  }
}
