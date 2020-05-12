package org.arig.robot.filters.common;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.arig.robot.filters.IFilter;
import org.springframework.util.Assert;

public class DifferenceFilter implements IFilter<Double, Double> {

    public static final String REFERENCE_VALUE_NULL_MESSAGE = "La valeur de référence ne peut être null";

    private final Double reference;

    @Getter
    @Accessors(fluent = true)
    private Double lastResult;

    public DifferenceFilter(Double reference) {
        super();

        Assert.notNull(reference, REFERENCE_VALUE_NULL_MESSAGE);
        this.reference = reference;
    }

    @Override
    public Double filter(Double value) {
        Assert.notNull(value, FILTER_VALUE_NULL_MESSAGE);
        lastResult = reference - value;
        return lastResult;
    }
}
