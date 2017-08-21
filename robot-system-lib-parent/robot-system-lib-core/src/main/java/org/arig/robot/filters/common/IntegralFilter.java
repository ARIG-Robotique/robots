package org.arig.robot.filters.common;

import lombok.Getter;
import org.arig.robot.filters.IFilter;
import org.springframework.util.Assert;

public class IntegralFilter implements IFilter<Double, Double> {

    public static final String INITIAL_VALUE_NULL_MESSAGE = "La valeur initial ne peut être null";

    private final Double initial;

    @Getter
    private Double sum;

    public IntegralFilter(Double initial) {
        super();

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
}
