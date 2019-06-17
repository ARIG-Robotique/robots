package org.arig.robot.filters.common;

import lombok.Getter;
import org.arig.robot.filters.IFilter;
import org.springframework.util.Assert;

public class DerivateFilter implements IFilter<Double, Double> {

    public static final String INITIAL_VALUE_NULL_MESSAGE = "La valeur initial ne peut être null";

    private final Double initial;

    @Getter
    private Double lastValue;

    public DerivateFilter(Double initial) {
        super();

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
        Double res = value - lastValue;
        lastValue = value;
        return res;
    }
}
