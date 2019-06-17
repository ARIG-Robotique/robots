package org.arig.robot.filters.average;

import java.util.function.BinaryOperator;

/**
 * @author gdepuille on 08/05/15.
 */
public class IntegerValueAverage extends AbstractCircularFifoValueAverage<Integer> {

    public IntegerValueAverage() {
        this(3);
    }

    public IntegerValueAverage(int limit) {
        super(limit);
    }

    @Override
    protected Integer identityValue() {
        return 0;
    }

    @Override
    protected BinaryOperator<Integer> reduceFunction() {
        return (a, b) -> a + b;
    }

    @Override
    protected Integer effectiveAverage(Integer reducedValue, int queueSize) {
        return reducedValue / queueSize;
    }
}
