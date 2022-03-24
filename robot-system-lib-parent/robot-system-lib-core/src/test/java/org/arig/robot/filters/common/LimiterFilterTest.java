package org.arig.robot.filters.common;

import org.apache.commons.lang3.StringUtils;
import org.arig.robot.filters.Filter;
import org.arig.robot.filters.common.LimiterFilter.LimiterType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class LimiterFilterTest {

    private static LimiterFilter simpleFilter;
    private static LimiterFilter mirrorFilter;
    private static LimiterFilter doubleMirrorFilter;

    @BeforeAll
    public static void initClass() {
        simpleFilter = new LimiterFilter(-10d, 10d);
        mirrorFilter = new LimiterFilter(2d, 10d, -10d, -20d);
        doubleMirrorFilter = new LimiterFilter(2d, 10d, LimiterType.MIRROR);
    }

    @Test
    public void valueIsNull() {
        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () -> simpleFilter.filter(null));
        Assertions.assertEquals(ex.getMessage(), Filter.FILTER_VALUE_NULL_MESSAGE);
    }

    @Test
    public void testNewMinNull() {
        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () -> new LimiterFilter(null, 10d));
        Assertions.assertEquals(ex.getMessage(), String.format(LimiterFilter.MIN_VALUE_NULL_MESSAGE, StringUtils.EMPTY));
    }

    @Test
    public void testNewMinMaxNull() {
        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () -> new LimiterFilter(null, null));
        Assertions.assertEquals(ex.getMessage(), String.format(LimiterFilter.MIN_VALUE_NULL_MESSAGE, StringUtils.EMPTY));
    }

    @Test
    public void testNewMaxNull() {
        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () -> new LimiterFilter(2d, null));
        Assertions.assertEquals(ex.getMessage(), String.format(LimiterFilter.MAX_VALUE_NULL_MESSAGE, StringUtils.EMPTY));
    }

    @Test
    public void testNewMaxGreaterThanMin() {
        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () -> new LimiterFilter(2d, 1d));
        Assertions.assertEquals(ex.getMessage(), LimiterFilter.MAX_GREATER_MIN_MESSAGE);
    }

    @Test
    public void testSimpleFilter() {
        double expected;
        for (double i = -15 ; i < 16 ; i++) {
            if (i < -10) {
                expected = -10;
            } else if (i > 10) {
                expected = 10;
            } else {
                expected = i;
            }
            Assertions.assertEquals(expected, simpleFilter.filter(i), 0);
        }
    }

    @Test
    public void testDoubleFilter() {
        double expected;
        for (double i = -25 ; i < 16 ; i++) {
            if (i < -20) {
                expected = -20;
            } else if (i > -10 && i < 2) {
                expected = (i >= 0) ? 2 : -10;
            } else if (i > 10) {
                expected = 10;
            } else {
                expected = i;
            }
            Assertions.assertEquals(expected, mirrorFilter.filter(i), 0);
        }
    }

    @Test
    public void testDoubleMirrorFilter() {
        double expected;
        for (double i = -15 ; i < 16 ; i++) {
            if (Math.abs(i) > 10) {
                expected = 10 * ((i >= 0) ? 1 : -1);
            } else if (Math.abs(i) < 2) {
                expected = 2 * ((i >= 0) ? 1 : -1);
            } else {
                expected = i;
            }
            Assertions.assertEquals(expected, doubleMirrorFilter.filter(i), 0);
        }
    }

    @Test
    public void testReset() {
        simpleFilter.reset();
    }
}
