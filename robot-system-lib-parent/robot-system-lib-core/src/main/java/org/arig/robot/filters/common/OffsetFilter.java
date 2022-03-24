package org.arig.robot.filters.common;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.arig.robot.filters.Filter;
import org.springframework.util.Assert;

public class OffsetFilter implements Filter<Double, Double> {

    public enum OffsetType {
        SIMPLE, MIRROR
    }

    public static final String OFFSET_VALUE_NULL_MESSAGE = "La valeur d'offset ne peut être null";

    private final Double offset;
    private final OffsetType type;

    @Getter
    @Accessors(fluent = true)
    private Double lastResult;

    public OffsetFilter(Double offset) {
        this(offset, OffsetType.SIMPLE);
    }

    public OffsetFilter(Double offset, OffsetType type) {
        super();

        Assert.notNull(offset, OFFSET_VALUE_NULL_MESSAGE);
        this.offset = offset;
        this.type = type;
    }

    @Override
    public Double filter(Double value) {
        Assert.notNull(value, FILTER_VALUE_NULL_MESSAGE);
        if (type == OffsetType.SIMPLE || (type == OffsetType.MIRROR && value >= 0)) {
            lastResult = value + offset;
        } else {
            lastResult = value - offset;
        }
        return lastResult;
    }
}
