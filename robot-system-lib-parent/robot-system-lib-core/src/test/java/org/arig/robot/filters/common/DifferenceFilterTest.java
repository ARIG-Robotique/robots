package org.arig.robot.filters.common;

import org.arig.robot.filters.IFilter;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

@RunWith(BlockJUnit4ClassRunner.class)
public class DifferenceFilterTest {

    private static DifferenceFilter impl;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @BeforeClass
    public static void initClass() {
        impl = new DifferenceFilter(10d);
    }

    @Test
    public void valueIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(IFilter.FILTER_VALUE_NULL_MESSAGE);

        impl.filter(null);
    }

    @Test
    public void testFilter() {
        Assert.assertEquals(0d, impl.filter(10d), 0);
        Assert.assertEquals(-10d, impl.filter(20d), 0);
        Assert.assertEquals(20d, impl.filter(-10d), 0);
    }

    @Test
    public void testReset() {
        impl.reset();
    }
}
