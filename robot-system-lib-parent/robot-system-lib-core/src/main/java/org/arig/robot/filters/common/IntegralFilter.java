package org.arig.robot.filters.common;

import lombok.Getter;
import org.arig.robot.filters.Filter;
import org.springframework.util.Assert;

public class IntegralFilter implements Filter<Double, Double> {

    public static final String INITIAL_VALUE_NULL_MESSAGE = "La valeur initial ne peut être null";

    protected final Double initial;

    @Getter
    private Double sum;

    public IntegralFilter(Double initial) {
        Assert.notNull(initial, INITIAL_VALUE_NULL_MESSAGE);
        this.initial = initial;
        this.sum = initial;
    }

    @Override
    public void reset() {
        sum = initial;
    }

    @Override
    public Double filter(Double value) {
        Assert.notNull(value, FILTER_VALUE_NULL_MESSAGE);
        sum = sum + value;
        return sum;
    }

    @Override
    public Double lastResult() {
        return sum;
    }
}
