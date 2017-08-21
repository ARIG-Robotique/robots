package org.arig.robot.filters.common;

import lombok.Getter;
import lombok.Setter;
import org.arig.robot.filters.IFilter;
import org.springframework.util.Assert;

public class ProportionalFilter implements IFilter<Double, Double> {

    public static final String GAIN_NULL_MESSAGE = "Le gain ne peut être null";

    @Getter
    private Double gain;

    public ProportionalFilter(Double gain) {
        super();
        setGain(gain);
    }

    @Override
    public Double filter(Double value) {
        Assert.notNull(value, FILTER_VALUE_NULL_MESSAGE);
        return gain * value;
    }

    public void setGain(Double gain) {
        Assert.notNull(gain, GAIN_NULL_MESSAGE);
        this.gain = gain;
    }
}
