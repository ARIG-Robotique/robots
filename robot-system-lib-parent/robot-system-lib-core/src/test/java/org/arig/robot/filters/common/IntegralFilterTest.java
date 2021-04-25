package org.arig.robot.filters.common;

import org.arig.robot.filters.IFilter;
import org.arig.robot.filters.common.IntegralFilter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

@RunWith(BlockJUnit4ClassRunner.class)
public class IntegralFilterTest {

    private static IntegralFilter impl;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @BeforeClass
    public static void initClass() {
        impl = new IntegralFilter(2d);
    }

    @Before
    public void beforeTest() {
        impl.reset();
    }

    @Test
    public void valueIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(IFilter.FILTER_VALUE_NULL_MESSAGE);

        impl.filter(null);
    }

    @Test
    public void testFilter() {
        Assert.assertEquals(3d, impl.filter(1d), 0);
        Assert.assertEquals(4d, impl.filter(1d), 0);
        Assert.assertEquals(5d, impl.filter(1d), 0);
        Assert.assertEquals(6d, impl.filter(1d), 0);
    }

    @Test
    public void testReset() {
        double res = 0;
        for (int i = 0 ; i < 10 ; i++) {
            res = impl.filter(1d);
        }
        Assert.assertEquals(12d, res, 0);

        impl.reset();
        Assert.assertEquals(2d, impl.filter(0d), 0);
    }
}
