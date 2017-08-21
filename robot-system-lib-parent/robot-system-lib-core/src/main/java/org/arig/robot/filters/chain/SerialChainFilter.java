package org.arig.robot.filters.chain;

import org.springframework.util.Assert;

import java.util.concurrent.atomic.AtomicReference;

public class SerialChainFilter<T> extends AbstractChainFilter<T> {

    @Override
    public T doFilter(T value) {
        final AtomicReference<T> res = new AtomicReference<>(value);
        getFilters().forEach(f -> res.set(f.filter(res.get())));
        return res.get();
    }
}
