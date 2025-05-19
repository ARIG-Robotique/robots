package org.arig.robot.filters.common;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.arig.robot.filters.Filter;

import java.util.Objects;

public class ChangeFilter<T> implements Filter<T, Boolean> {

  private final T initial;

  @Getter
  private T lastValue;

  @Getter
  @Accessors(fluent = true)
  private Boolean lastResult;

  public ChangeFilter(T initial) {
    this.initial = initial;
    this.lastValue = initial;
  }

  @Override
  public void reset() {
    lastValue = initial;
  }

  @Override
  public Boolean filter(T value) {
    boolean result = false;
    if (!Objects.equals(value, lastValue)) {
      lastValue = value;
      result = true;
    }
    lastResult = result;
    return lastResult;
  }
}
