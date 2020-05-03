package org.arig.robot.filters.common;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.arig.robot.filters.IFilter;
import org.springframework.util.Assert;

public class ChangeFilter<T> implements IFilter<T, Boolean> {

    public static final String INITIAL_VALUE_NULL_MESSAGE = "La valeur initiale ne peut être null";

    private final T initial;

    @Getter
    private T lastValue;

    @Getter
    @Accessors(fluent = true)
    private Boolean lastResult;

    public ChangeFilter(T initial) {
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
    public Boolean filter(T value) {
        Assert.notNull(value, FILTER_VALUE_NULL_MESSAGE);

        boolean result = false;
        if (!value.equals(lastValue)) {
            lastValue = value;
            result = true;
        }
        lastResult = result;
        return lastResult;
    }
}
