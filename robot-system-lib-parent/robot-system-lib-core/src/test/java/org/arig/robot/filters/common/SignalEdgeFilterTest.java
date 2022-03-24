package org.arig.robot.filters.common;

import org.arig.robot.filters.Filter;
import org.arig.robot.filters.common.SignalEdgeFilter.Type;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class SignalEdgeFilterTest {

    private static SignalEdgeFilter risingFilter;
    private static SignalEdgeFilter fallingFilter;
    private static SignalEdgeFilter risingAndFallingFilter;

    @BeforeAll
    public static void initClass() {
        risingFilter = new SignalEdgeFilter(false, Type.RISING);
        fallingFilter = new SignalEdgeFilter(false, Type.FALLING);
        risingAndFallingFilter = new SignalEdgeFilter(false, Type.RISING, Type.FALLING);
    }

    @BeforeEach
    public void beforeTest() {
        risingFilter.reset();
        fallingFilter.reset();
        risingAndFallingFilter.reset();
    }

    @Test
    public void lastResult() {
        Assertions.assertEquals(false, risingFilter.lastResult());
        risingFilter.filter(true);
        Assertions.assertEquals(true, risingFilter.lastResult());
    }

    @Test
    public void risingValueIsNull() {
        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () -> risingFilter.filter(null));
        Assertions.assertEquals(ex.getMessage(), Filter.FILTER_VALUE_NULL_MESSAGE);
    }

    @Test
    public void testRisingFilter() {
        Assertions.assertEquals(false, risingFilter.filter(false));
        Assertions.assertEquals(true, risingFilter.filter(true));
        Assertions.assertEquals(false, risingFilter.filter(true));
        Assertions.assertEquals(false, risingFilter.filter(false));
    }

    @Test
    public void testFallingFilter() {
        Assertions.assertEquals(false, fallingFilter.filter(false));
        Assertions.assertEquals(false, fallingFilter.filter(true));
        Assertions.assertEquals(false, fallingFilter.filter(true));
        Assertions.assertEquals(true, fallingFilter.filter(false));
    }

    @Test
    public void testRisingAndFallingFilter() {
        Assertions.assertEquals(false, risingAndFallingFilter.filter(false));
        Assertions.assertEquals(true, risingAndFallingFilter.filter(true));
        Assertions.assertEquals(false, risingAndFallingFilter.filter(true));
        Assertions.assertEquals(true, risingAndFallingFilter.filter(false));
    }

    @Test
    public void testReset() {
        risingFilter.filter(true);
        Assertions.assertEquals(true, risingFilter.getLastValue());
        risingFilter.reset();
        Assertions.assertEquals(false, risingFilter.getLastValue());
    }

    @Test
    public void testLastValue() {
        boolean last = risingFilter.getLastValue();
        boolean value = true;
        for (double i = 0; i < 10; i++) {
            risingFilter.filter(value);
            Assertions.assertNotEquals(last, risingFilter.getLastValue());
            Assertions.assertEquals(value, risingFilter.getLastValue());
            last = value;
            value = !value;
        }
    }
}
