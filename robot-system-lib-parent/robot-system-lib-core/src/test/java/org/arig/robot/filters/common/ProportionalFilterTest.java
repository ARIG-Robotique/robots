package org.arig.robot.filters.common;

import org.arig.robot.filters.IFilter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class ProportionalFilterTest {

    private static ProportionalFilter impl;

    @BeforeAll
    public static void initClass() {
        impl = new ProportionalFilter(2d);
    }

    @Test
    public void valueIsNull() {
        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () -> impl.filter(null));
        Assertions.assertEquals(ex.getMessage(), IFilter.FILTER_VALUE_NULL_MESSAGE);
    }

    @Test
    public void testFilter() {
        Assertions.assertEquals(2d, impl.filter(1d), 0);
        Assertions.assertEquals(4d, impl.filter(2d), 0);
        Assertions.assertEquals(6d, impl.filter(3d), 0);
        Assertions.assertEquals(8d, impl.filter(4d), 0);
    }

    @Test
    public void testReset() {
        impl.reset();
    }
}
