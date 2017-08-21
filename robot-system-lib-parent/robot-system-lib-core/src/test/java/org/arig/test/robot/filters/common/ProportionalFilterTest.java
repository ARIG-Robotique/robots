package org.arig.test.robot.filters.common;

import org.arig.robot.filters.IFilter;
import org.arig.robot.filters.common.ProportionalFilter;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

@RunWith(BlockJUnit4ClassRunner.class)
public class ProportionalFilterTest {

    private static ProportionalFilter impl;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @BeforeClass
    public static void initClass() {
        impl = new ProportionalFilter(2d);
    }

    @Test
    public void valueIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(IFilter.FILTER_VALUE_NULL_MESSAGE);

        impl.filter(null);
    }

    @Test
    public void testFilter() {
        Assert.assertEquals(2d, impl.filter(1d), 0);
        Assert.assertEquals(4d, impl.filter(2d), 0);
        Assert.assertEquals(6d, impl.filter(3d), 0);
        Assert.assertEquals(8d, impl.filter(4d), 0);
    }

    @Test
    public void testReset() {
        impl.reset();
    }
}
