package org.arig.robot.filters.range;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.arig.robot.filters.Filter;

@RequiredArgsConstructor
public class AroundValueDoubleFilter implements Filter<Double, Boolean> {

  @Getter
  @Accessors(fluent = true)
  private Boolean lastResult;

  private final double aroundValue;

  @Setter
  @Accessors(fluent = true)
  private double base = 0;

  @Override
  public Boolean filter(Double value) {
    if (value >= base - aroundValue && value <= base + aroundValue) {
      lastResult = true;
    } else {
      lastResult = false;
    }

    return lastResult;
  }
}
