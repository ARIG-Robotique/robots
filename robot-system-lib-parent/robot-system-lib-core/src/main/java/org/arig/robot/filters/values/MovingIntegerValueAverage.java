package org.arig.robot.filters.values;

import org.apache.commons.collections4.queue.CircularFifoQueue;

/**
 * @author gdepuille on 08/05/15.
 */
public class MovingIntegerValueAverage implements IAverage<Integer> {

    private CircularFifoQueue<Integer> queue;

    public MovingIntegerValueAverage() {
        this(3);
    }

    public MovingIntegerValueAverage(int limit) {
        queue = new CircularFifoQueue<>(limit);
    }

    public void setLimit(int newLimit) {
        CircularFifoQueue<Integer> tmp = new CircularFifoQueue<>(newLimit);
        tmp.addAll(queue);
        queue = tmp;
    }

    @Override
    public int size() {
        return queue.size();
    }

    @Override
    public void reset() {
        queue.clear();
    }

    @Override
    public Integer average(Integer newValue) {
        queue.offer(newValue);
        int res = queue.parallelStream().reduce(0, (a, b) -> a + b);
        return res / queue.size();
    }
}
