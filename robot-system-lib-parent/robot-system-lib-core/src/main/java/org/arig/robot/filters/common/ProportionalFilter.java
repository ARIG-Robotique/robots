package org.arig.robot.filters.common;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.arig.robot.filters.Filter;
import org.springframework.util.Assert;

public class ProportionalFilter implements Filter<Double, Double> {

    public static final String GAIN_NULL_MESSAGE = "Le gain ne peut être null";

    @Getter
    private Double gain;

    @Getter
    @Accessors(fluent = true)
    private Double lastResult;

    public ProportionalFilter(Double gain) {
        super();
        setGain(gain);
    }

    @Override
    public Double filter(Double value) {
        Assert.notNull(value, FILTER_VALUE_NULL_MESSAGE);
        lastResult = gain * value;
        return lastResult;
    }

    public void setGain(Double gain) {
        Assert.notNull(gain, GAIN_NULL_MESSAGE);
        this.gain = gain;
    }
}
