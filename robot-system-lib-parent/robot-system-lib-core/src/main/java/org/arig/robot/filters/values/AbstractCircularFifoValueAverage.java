package org.arig.robot.filters.values;

import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.util.function.BinaryOperator;

/**
 * Classe de base pour les calcul de moyenne en utilisant un FIFO circulaire.
 *
 * @author gdepuille on 14/05/17.
 */
public abstract class AbstractCircularFifoValueAverage<T> implements IAverage<T> {

    private CircularFifoQueue<T> queue;

    AbstractCircularFifoValueAverage(int limit) {
        queue = new CircularFifoQueue<>(limit);
    }

    public final void setLimit(int newLimit) {
        CircularFifoQueue<T> tmp = new CircularFifoQueue<>(newLimit);
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
    public final T average(T newValue) {
        queue.offer(newValue);
        T res = queue.parallelStream().reduce(identityValue(), reduceFunction());
        return effectiveAverage(res, queue.size());
    }

    protected abstract T identityValue();
    protected abstract BinaryOperator<T> reduceFunction();
    protected abstract T effectiveAverage(T reducedValue, int queueSize);
}
