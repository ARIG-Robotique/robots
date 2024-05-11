package org.arig.robot.filters.average;

import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * @author gdepuille on 08/05/15.
 */
public class BooleanValueAverage implements Average<Boolean> {

    private final DoubleValueAverage delegate;

    @Getter
    @Accessors(fluent = true)
    private Boolean lastResult;

    public BooleanValueAverage() {
        this(3);
    }

    public BooleanValueAverage(int limit) {
        delegate = new DoubleValueAverage(limit);
    }

    @Override
    public void setNbValues(int nbValues) {
        delegate.setNbValues(nbValues);
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public void reset() {
        delegate.reset();
    }

    @Override
    public Boolean filter(Boolean value) {
        Double result = delegate.filter((double) (value ? 1 : 0));
        lastResult = (result >= 0.5);
        return lastResult;
    }
}
