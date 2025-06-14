package org.arig.robot.filters.chain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.arig.robot.filters.Filter;
import org.springframework.util.Assert;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractChainFilter<T> implements Filter<T, T> {

  @Getter(AccessLevel.PROTECTED)
  private final List<Filter<T, T>> filters = new LinkedList<>();

  @Getter
  @Accessors(fluent = true)
  private T lastResult;

  protected abstract T doFilter(T value);

  public void addFilter(Filter<T, T> filter) {
    filters.add(filter);
  }

  @Override
  public final T filter(final T value) {
    Assert.notNull(value, FILTER_VALUE_NULL_MESSAGE);
    lastResult = doFilter(value);
    return lastResult;
  }

  @Override
  public final void reset() {
    filters.forEach(Filter::reset);
  }
}
