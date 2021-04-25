package org.arig.robot.filters.common;

import org.arig.robot.filters.common.OffsetFilter;
import org.arig.robot.filters.common.OffsetFilter.OffsetType;
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

    private static OffsetFilter simpleOffset;
    private static OffsetFilter mirrorOffset;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @BeforeClass
    public static void initClass() {
        simpleOffset = new OffsetFilter(10d);
        mirrorOffset = new OffsetFilter(10d, OffsetType.MIRROR);
    }

    @Before
    public void beforeTest() {
        simpleOffset.reset();
        mirrorOffset.reset();
    }

    @Test
    public void testSimpleFilter() {
        Assert.assertEquals(9d, simpleOffset.filter(-1d), 0d);
        Assert.assertEquals(20d, simpleOffset.filter(10d), 0d);
        Assert.assertEquals(20d, simpleOffset.filter(10d), 0d);
        Assert.assertEquals(19d, simpleOffset.filter(9d), 0d);
    }

    @Test
    public void testMirrorFilter() {
        Assert.assertEquals(-11d, mirrorOffset.filter(-1d), 0d);
        Assert.assertEquals(20d, mirrorOffset.filter(10d), 0d);
        Assert.assertEquals(-20d, mirrorOffset.filter(-10d), 0d);
        Assert.assertEquals(11d, mirrorOffset.filter(1d), 0d);
    }
}
