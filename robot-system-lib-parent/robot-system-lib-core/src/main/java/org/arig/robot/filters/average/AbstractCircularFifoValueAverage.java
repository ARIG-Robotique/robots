package org.arig.robot.filters.average;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.springframework.util.Assert;

import java.util.function.BinaryOperator;

/**
 * Classe de base pour les calculs de moyenne en utilisant une FIFO circulaire.
 *
 * @author gdepuille on 14/05/17.
 */
public abstract class AbstractCircularFifoValueAverage<T> implements Average<T> {

    private CircularFifoQueue<T> queue;

    @Getter
    @Accessors(fluent = true)
    private T lastResult;

    AbstractCircularFifoValueAverage(int nbValues) {
        queue = new CircularFifoQueue<>(nbValues);
    }

    public final void setNbValues(int newNbValues) {
        CircularFifoQueue<T> tmp = new CircularFifoQueue<>(newNbValues);
        tmp.addAll(queue);
        queue = tmp;
    }

    @Override
    public final int size() {
        return queue.size();
    }

    @Override
    public final void reset() {
        queue.clear();
    }

    @Override
    public final T filter(T newValue) {
        Assert.notNull(newValue, FILTER_VALUE_NULL_MESSAGE);
        queue.offer(newValue);
        T res = queue.parallelStream().reduce(identityValue(), reduceFunction());
        lastResult = effectiveAverage(res, queue.size());
        return lastResult;
    }

    protected abstract T identityValue();
    protected abstract BinaryOperator<T> reduceFunction();
    protected abstract T effectiveAverage(T reducedValue, int queueSize);
}
