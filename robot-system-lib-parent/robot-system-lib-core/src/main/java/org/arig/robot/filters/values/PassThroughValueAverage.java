package org.arig.robot.filters.values;

import lombok.Data;

/**
 * Implementation qui ne calcul rien
 *
 * @author gdepuille on 08/05/15.
 */
@Data
public class PassThroughValueAverage<T> implements IAverage<T> {

    private int limit;

    @Override
    public void reset() {
    }

    @Override
    public T average(T newValue) {
        return newValue;
    }
}
