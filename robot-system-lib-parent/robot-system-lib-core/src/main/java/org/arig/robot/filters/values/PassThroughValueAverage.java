package org.arig.robot.filters.values;

import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Implementation qui ne calcul rien
 *
 * @author gdepuille on 08/05/15.
 */
public class PassThroughValueAverage<T> implements IAverage<T> {

    @Getter
    @Accessors(fluent = true)
    private final int size = 0;

    @Override
    public void reset() { }

    @Override
    public void setLimit(int limit) { }

    @Override
    public T average(T newValue) {
        return newValue;
    }
}
