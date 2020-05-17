package org.arig.test.robot.filters.common;

import org.arig.robot.filters.common.OffsetFilter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

@RunWith(BlockJUnit4ClassRunner.class)
public class OffsetFilterTest {

    private static OffsetFilter filter;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @BeforeClass
    public static void initClass() {
        filter = new OffsetFilter(10d);
    }

    @Before
    public void beforeTest() {
        filter.reset();
    }

    @Test
    public void testFilter() {
        Assert.assertEquals(9d, filter.filter(-1d), 0d);
        Assert.assertEquals(20d, filter.filter(10d), 0d);
        Assert.assertEquals(20d, filter.filter(10d), 0d);
        Assert.assertEquals(19d, filter.filter(9d), 0d);
    }
}
