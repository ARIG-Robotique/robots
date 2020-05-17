package org.arig.robot.filters.common;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.arig.robot.filters.IFilter;
import org.springframework.util.Assert;

public class OffsetFilter implements IFilter<Double, Double> {

    public static final String OFFSET_VALUE_NULL_MESSAGE = "La valeur d'offset ne peut être null";

    private final Double offset;

    @Getter
    @Accessors(fluent = true)
    private Double lastResult;

    public OffsetFilter(Double offset) {
        super();

        Assert.notNull(offset, OFFSET_VALUE_NULL_MESSAGE);
        this.offset = offset;
    }

    @Override
    public Double filter(Double value) {
        Assert.notNull(value, FILTER_VALUE_NULL_MESSAGE);

        lastResult = value + offset;
        return lastResult;
    }
}
