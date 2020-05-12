package org.arig.test.robot.filters.common;

import org.apache.commons.lang3.StringUtils;
import org.arig.robot.filters.IFilter;
import org.arig.robot.filters.common.LimiterFilter;
import org.arig.robot.filters.common.LimiterFilter.LimiterType;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

@RunWith(BlockJUnit4ClassRunner.class)
public class LimiterFilterTest {

    private static LimiterFilter simpleFilter;
    private static LimiterFilter doubleFilter;
    private static LimiterFilter doubleMirrorFilter;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @BeforeClass
    public static void initClass() {
        simpleFilter = new LimiterFilter(-10d, 10d);
        doubleFilter = new LimiterFilter(2d, 10d, -10d, -20d);
        doubleMirrorFilter = new LimiterFilter(2d, 10d, LimiterType.DOUBLE);
    }

    @Test
    public void valueIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(IFilter.FILTER_VALUE_NULL_MESSAGE);

        simpleFilter.filter(null);
    }

    @Test
    public void testNewMinNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(String.format(LimiterFilter.MIN_VALUE_NULL_MESSAGE, StringUtils.EMPTY));


        new LimiterFilter(null, 10d);
    }

    @Test
    public void testNewMinMaxNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(String.format(LimiterFilter.MIN_VALUE_NULL_MESSAGE, StringUtils.EMPTY));

        new LimiterFilter(null, null);
    }

    @Test
    public void testNewMaxNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(String.format(LimiterFilter.MAX_VALUE_NULL_MESSAGE, StringUtils.EMPTY));

        new LimiterFilter(2d, null);
    }

    @Test
    public void testNewMaxGreaterThanMin() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(LimiterFilter.MAX_GREATER_MIN_MESSAGE);

        new LimiterFilter(2d, 1d);
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
            Assert.assertEquals(expected, simpleFilter.filter(i), 0);
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
            Assert.assertEquals(expected, doubleFilter.filter(i), 0);
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
            Assert.assertEquals(expected, doubleMirrorFilter.filter(i), 0);
        }
    }

    @Test
    public void testReset() {
        simpleFilter.reset();
    }
}
