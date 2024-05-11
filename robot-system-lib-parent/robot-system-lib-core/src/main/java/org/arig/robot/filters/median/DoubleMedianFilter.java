package org.arig.robot.filters.median;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.arig.robot.filters.Filter;

import java.util.List;
import java.util.stream.Collectors;

public class DoubleMedianFilter implements Filter<Double, Double> {

  private final CircularFifoQueue<Double> queue;

  @Getter
  @Accessors(fluent = true)
  private Double lastResult = 0.0;

  public DoubleMedianFilter(int nbValues) {
    queue = new CircularFifoQueue<>(nbValues);
    reset();
  }

  @Override
  public Double filter(Double value) {
    queue.add(value);
    List<Double> sortedValue = queue.stream().sorted().collect(Collectors.toList());
    int size = sortedValue.size();
    if (size % 2 == 0) {
      lastResult = (sortedValue.get(size / 2 - 1) + sortedValue.get(size / 2)) / 2;
    } else {
      lastResult = sortedValue.get(size / 2);
    }
    return lastResult;
  }

  @Override
  public void reset() {
    queue.clear();
    lastResult = 0.0;
  }

  public void removeLastValue() {
    queue.remove(queue.size() - 1);
  }

}
