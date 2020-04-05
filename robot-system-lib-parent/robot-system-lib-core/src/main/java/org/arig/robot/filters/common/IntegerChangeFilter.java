package org.arig.robot.filters.common;

import lombok.Getter;
import org.arig.robot.filters.IFilter;
import org.springframework.util.Assert;

public class IntegerChangeFilter implements IFilter<Integer, Boolean> {

    public static final String INITIAL_VALUE_NULL_MESSAGE = "La valeur initial ne peut être null";

    private final Integer initial;

    @Getter
    private Integer lastValue;

    public IntegerChangeFilter(Integer initial) {
        super();

        Assert.notNull(initial, INITIAL_VALUE_NULL_MESSAGE);
        this.initial = initial;
        this.lastValue = initial;
    }

    @Override
    public void reset() {
        lastValue = initial;
    }

    @Override
    public Boolean filter(Integer value) {
        Assert.notNull(value, FILTER_VALUE_NULL_MESSAGE);

        boolean result = false;
        if (!value.equals(lastValue)) {
            lastValue = value;
            result = true;
        }
        return result;
    }
}
