package org.arig.robot.filters.common;

import org.arig.robot.filters.Filter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class DifferenceFilterTest {

    private static DifferenceFilter impl;

    @BeforeAll
    public static void initClass() {
        impl = new DifferenceFilter(10d);
    }

    @Test
    public void valueIsNull() {
        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () -> impl.filter(null));
        Assertions.assertEquals(ex.getMessage(), Filter.FILTER_VALUE_NULL_MESSAGE);
    }

    @Test
    public void testFilter() {
        Assertions.assertEquals(0d, impl.filter(10d), 0);
        Assertions.assertEquals(-10d, impl.filter(20d), 0);
        Assertions.assertEquals(20d, impl.filter(-10d), 0);
    }

    @Test
    public void testReset() {
        impl.reset();
    }
}
