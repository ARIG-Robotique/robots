package org.arig.robot.filters.common;

import org.arig.robot.filters.IFilter;
import org.springframework.util.Assert;

public class DifferenceFilter implements IFilter<Double, Double> {

    public static final String REFERENCE_VALUE_NULL_MESSAGE = "La valeur de référence ne peut être null";

    private final Double reference;

    public DifferenceFilter(Double reference) {
        super();

        Assert.notNull(reference, REFERENCE_VALUE_NULL_MESSAGE);
        this.reference = reference;
    }

    @Override
    public Double filter(Double value) {
        Assert.notNull(value, FILTER_VALUE_NULL_MESSAGE);
        return reference - value;
    }
}
