package org.arig.robot.filters.chain;

import lombok.AccessLevel;
import lombok.Getter;
import org.arig.robot.filters.IFilter;
import org.springframework.util.Assert;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractChainFilter<T> implements IFilter<T, T> {

    @Getter(AccessLevel.PROTECTED)
    private final List<IFilter<T, T>> filters = new LinkedList<>();

    protected abstract T doFilter(T value);

    public void addFilter(IFilter<T, T> filter) {
        filters.add(filter);
    }

    @Override
    public final T filter(final T value) {
        Assert.notNull(value, FILTER_VALUE_NULL_MESSAGE);
        return doFilter(value);
    }

    @Override
    public final void reset() {
        filters.forEach(IFilter::reset);
    }
}
