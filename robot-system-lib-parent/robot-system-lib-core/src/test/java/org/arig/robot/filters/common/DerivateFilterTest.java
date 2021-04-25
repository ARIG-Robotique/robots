package org.arig.robot.filters.common;

import org.arig.robot.filters.IFilter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

@RunWith(BlockJUnit4ClassRunner.class)
public class DerivateFilterTest {

    private static DerivateFilter impl;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @BeforeClass
    public static void initClass() {
        impl = new DerivateFilter(2d);
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
        Assert.assertEquals(-1d, impl.filter(1d), 0);
        Assert.assertEquals(1d, impl.filter(2d), 0);
        Assert.assertEquals(2d, impl.filter(4d), 0);
        Assert.assertEquals(-3d, impl.filter(1d), 0);
    }

    @Test
    public void testReset() {
        double res = 0;
        for (int i = 0 ; i < 10 ; i++) {
            res = impl.filter(i * 2d);
        }
        Assert.assertEquals(2d, res, 0);

        impl.reset();
        Assert.assertEquals(-2d, impl.filter(0d), 0);
    }

    @Test
    public void testLastValue() {
        double last = impl.getLastValue();
        for (double i = 0 ; i < 10 ; i++) {
            impl.filter(i);
            Assert.assertNotEquals(last, impl.getLastValue());
            Assert.assertEquals(i, impl.getLastValue(), 0d);
            last = i;
        }
    }
}
