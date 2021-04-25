package org.arig.robot.filters.common;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

@RunWith(BlockJUnit4ClassRunner.class)
public class ChangeFilterTest {

    private static ChangeFilter<Integer> filter;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @BeforeClass
    public static void initClass() {
        filter = new ChangeFilter<>(-1);
    }

    @Before
    public void beforeTest() {
        filter.reset();
    }

    @Test
    public void testFilter() {
        Assert.assertEquals(false, filter.filter(-1));
        Assert.assertEquals(true, filter.filter(10));
        Assert.assertEquals(false, filter.filter(10));
        Assert.assertEquals(true, filter.filter(9));
    }

    @Test
    public void testReset() {
        filter.filter(12);
        Assert.assertEquals(12, filter.getLastValue(), 0);
        filter.reset();
        Assert.assertEquals(-1, filter.getLastValue(), 0);
    }
}
