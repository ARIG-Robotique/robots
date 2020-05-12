package org.arig.robot.filters.common;

import lombok.Getter;
import org.apache.commons.lang3.ArrayUtils;
import org.arig.robot.filters.IFilter;
import org.springframework.util.Assert;

public class SignalEdgeFilter implements IFilter<Boolean, Boolean> {

    public static final String INITIAL_VALUE_NULL_MESSAGE = "La valeur initial ne peut être null";

    public enum Type {
        RISING, FALLING
    }

    private final Boolean initial;
    private final Type[] types;

    @Getter
    private Boolean lastValue;

    public SignalEdgeFilter(Boolean initial, Type ... types) {
        super();

        Assert.notNull(initial, INITIAL_VALUE_NULL_MESSAGE);
        this.initial = initial;
        this.lastValue = initial;
        this.types = types;
    }

    @Override
    public void reset() {
        lastValue = initial;
    }

    @Override
    public Boolean filter(Boolean value) {
        Assert.notNull(value, FILTER_VALUE_NULL_MESSAGE);

        boolean result;
        if (types.length == 1) {
            result = ArrayUtils.contains(types, Type.RISING) ? risingEdge(value) : fallingEdge(value);
        } else {
            result = risingEdge(value) || fallingEdge(value);
        }

        lastValue = value;
        return result;
    }

    @Override
    public Boolean lastResult() {
        return lastValue;
    }

    private boolean risingEdge(boolean value) {
        return value && !lastValue;
    }

    private boolean fallingEdge(boolean value) {
        return !value && lastValue;
    }
}
