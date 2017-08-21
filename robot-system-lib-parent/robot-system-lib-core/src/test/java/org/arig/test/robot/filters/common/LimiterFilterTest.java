package org.arig.test.robot.filters.common;

import org.arig.robot.filters.IFilter;
import org.arig.robot.filters.common.LimiterFilter;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

@RunWith(BlockJUnit4ClassRunner.class)
public class LimiterFilterTest {

    private static LimiterFilter impl;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @BeforeClass
    public static void initClass() {
        impl = new LimiterFilter(2d, 10d);
    }

    @Test
    public void valueIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(IFilter.FILTER_VALUE_NULL_MESSAGE);

        impl.filter(null);
    }

    @Test
    public void testNewMinNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(LimiterFilter.MIN_VALUE_NULL_MESSAGE);

        new LimiterFilter(null, null);
    }

    @Test
    public void testNewMaxNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(LimiterFilter.MAX_VALUE_NULL_MESSAGE);

        new LimiterFilter(2d, null);
    }

    @Test
    public void testNewMaxGreaterThanMin() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(LimiterFilter.MAX_GREATER_MIN_MESSAGE);

        new LimiterFilter(2d, 1d);
    }

    @Test
    public void testFilter() {
        Assert.assertEquals(2d, impl.filter(1d), 0);
        Assert.assertEquals(2d, impl.filter(2d), 0);
        Assert.assertEquals(3d, impl.filter(3d), 0);
        Assert.assertEquals(8d, impl.filter(8d), 0);
        Assert.assertEquals(10d, impl.filter(10d), 0);
        Assert.assertEquals(10d, impl.filter(15d), 0);
    }

    @Test
    public void testReset() {
        impl.reset();
    }
}
