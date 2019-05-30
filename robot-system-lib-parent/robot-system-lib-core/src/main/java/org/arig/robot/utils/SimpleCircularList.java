package org.arig.robot.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;

public class SimpleCircularList<T> extends ArrayList<T> {

    private final int size;
    private int head = 0;

    public SimpleCircularList(int size, Function<Integer, T> initSupplier) {
        super(size);
        this.size = size;

        for (int i = 0; i < size; i++) {
            super.add(initSupplier == null ? null : initSupplier.apply(i));
        }
    }

    @Override
    public T set(int index, T element) {
        return super.set(realIndex(index), element);
    }

    @Override
    public T get(int index) {
        return super.get(realIndex(index));
    }

    /**
     * Rotation des données "vers la gauche"
     */
    public void rotate(int val) {
        head += val;

        if (head >= size) {
            head -= size;
        }
        if (head < 0) {
            head += size;
        }
    }

    private int realIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IllegalArgumentException();
        }

        index += head;

        if (index >= size) {
            index -= size;
        }
        if (index < 0) {
            index += size;
        }

        return index;
    }

    public void reset() {
        for (int i = 0; i < size; i++) {
            set(i, null);
        }
    }

    @Override
    public void clear() {
        throw new IllegalArgumentException();
    }

    @Override
    public T remove(int index) {
        throw new IllegalArgumentException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new IllegalArgumentException();
    }

    @Override
    public boolean remove(Object o) {
        throw new IllegalArgumentException();
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        throw new IllegalArgumentException();
    }

    @Override
    public boolean removeIf(Predicate<? super T> filter) {
        throw new IllegalArgumentException();
    }

    @Override
    public boolean add(T t) {
        throw new IllegalArgumentException();
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        throw new IllegalArgumentException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        throw new IllegalArgumentException();
    }

    @Override
    public void add(int index, T element) {
        throw new IllegalArgumentException();
    }
}
