package org.arig.robot.filters.common;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.arig.robot.filters.IFilter;

import java.util.Objects;

public class ChangeFilter<T> implements IFilter<T, Boolean> {

    private final T initial;

    @Getter
    private T lastValue;

    @Getter
    @Accessors(fluent = true)
    private Boolean lastResult;

    public ChangeFilter(T initial) {
        super();

        this.initial = initial;
        this.lastValue = initial;
    }

    @Override
    public void reset() {
        lastValue = initial;
    }

    @Override
    public Boolean filter(T value) {
        boolean result = false;
        if (!Objects.equals(value, lastValue)) {
            lastValue = value;
            result = true;
        }
        lastResult = result;
        return lastResult;
    }
}
