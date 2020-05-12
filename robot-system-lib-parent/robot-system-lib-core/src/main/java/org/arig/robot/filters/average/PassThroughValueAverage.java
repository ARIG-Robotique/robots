package org.arig.robot.filters.average;

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

    @Getter
    @Accessors(fluent = true)
    private T lastResult;

    @Override
    public void setNbValues(int nbValues) { }

    @Override
    public T filter(T newValue) {
        lastResult = newValue;
        return lastResult;
    }
}
