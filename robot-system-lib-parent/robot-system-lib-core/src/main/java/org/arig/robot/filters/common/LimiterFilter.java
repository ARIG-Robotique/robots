package org.arig.robot.filters.common;

import org.arig.robot.filters.IFilter;
import org.springframework.util.Assert;

public class LimiterFilter implements IFilter<Double, Double> {

    public static final String MIN_VALUE_NULL_MESSAGE = "Min ne peut être null";
    public static final String MAX_VALUE_NULL_MESSAGE = "Max ne peut être null";
    public static final String MAX_GREATER_MIN_MESSAGE = "Max doit être supérieur a Min";

    private final Double min, max;

    public LimiterFilter(Double min, Double max) {
        super();

        Assert.notNull(min, MIN_VALUE_NULL_MESSAGE);
        Assert.notNull(max, MAX_VALUE_NULL_MESSAGE);
        Assert.isTrue(max > min, MAX_GREATER_MIN_MESSAGE);

        this.min = min;
        this.max = max;
    }

    @Override
    public Double filter(Double value) {
        Assert.notNull(value, FILTER_VALUE_NULL_MESSAGE);
        return value < min ? min : (value > max) ? max : value;
    }
}
