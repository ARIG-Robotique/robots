package org.arig.robot.filters.common;

import lombok.Getter;
import org.springframework.util.Assert;

public class IntegralLimitedFilter extends IntegralFilter {

    @Getter
    private Double limitedSum;

    private final LimiterFilter limiter;

    public IntegralLimitedFilter(Double initial) {
        this(initial, new LimiterFilter(0d, Double.MAX_VALUE, LimiterFilter.LimiterType.MIRROR));
    }

    public IntegralLimitedFilter(Double initial, LimiterFilter limiter) {
        super(initial);
        this.limitedSum = initial;
        this.limiter = limiter;
    }

    @Override
    public void reset() {
        limitedSum = initial;
        limiter.reset();
    }

    @Override
    public Double filter(Double value) {
        Assert.notNull(value, FILTER_VALUE_NULL_MESSAGE);
        limitedSum = limitedSum + value;
        limitedSum = limiter.filter(limitedSum);
        return limitedSum;
    }

    @Override
    public Double lastResult() {
        return limitedSum;
    }
}
