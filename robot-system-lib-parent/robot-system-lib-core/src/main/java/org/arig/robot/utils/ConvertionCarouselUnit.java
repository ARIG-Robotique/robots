package org.arig.robot.utils;

import lombok.Getter;

public final class ConvertionCarouselUnit {

    @Getter
    private final long countPerIndex;

    public ConvertionCarouselUnit(final long countPerIndex) {
        this.countPerIndex = countPerIndex;
    }

    public long indexToPulse(final int val) {
        return val * countPerIndex;
    }
    public int pulseToIndex(final long val) {
        return (int) (val / countPerIndex);
    }
}
