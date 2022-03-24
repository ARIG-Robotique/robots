package org.arig.robot.filters.common;

import org.arig.robot.filters.Filter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class DerivateFilterTest {

    private static DerivateFilter impl;

    @BeforeAll
    public static void initClass() {
        impl = new DerivateFilter(2d);
    }

    @BeforeEach
    public void beforeTest() {
        impl.reset();
    }

    @Test
    public void valueIsNull() {
        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () -> impl.filter(null));
        Assertions.assertEquals(ex.getMessage(), Filter.FILTER_VALUE_NULL_MESSAGE);
    }

    @Test
    public void testFilter() {
        Assertions.assertEquals(-1d, impl.filter(1d), 0);
        Assertions.assertEquals(1d, impl.filter(2d), 0);
        Assertions.assertEquals(2d, impl.filter(4d), 0);
        Assertions.assertEquals(-3d, impl.filter(1d), 0);
    }

    @Test
    public void testReset() {
        double res = 0;
        for (int i = 0 ; i < 10 ; i++) {
            res = impl.filter(i * 2d);
        }
        Assertions.assertEquals(2d, res, 0);

        impl.reset();
        Assertions.assertEquals(-2d, impl.filter(0d), 0);
    }

    @Test
    public void testLastValue() {
        double last = impl.getLastValue();
        for (double i = 0 ; i < 10 ; i++) {
            impl.filter(i);
            Assertions.assertNotEquals(last, impl.getLastValue());
            Assertions.assertEquals(i, impl.getLastValue(), 0d);
            last = i;
        }
    }
}
