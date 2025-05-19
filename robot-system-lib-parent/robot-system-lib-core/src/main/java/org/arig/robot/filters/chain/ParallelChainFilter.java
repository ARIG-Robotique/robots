package org.arig.robot.filters.chain;

public class ParallelChainFilter extends AbstractChainFilter<Double> {

  @Override
  public Double doFilter(Double value) {
    return getFilters().parallelStream()
      .mapToDouble(f -> f.filter(value))
      .sum();
  }
}
