package org.arig.test.robot.filters.common;

import org.arig.robot.filters.IFilter;
import org.arig.robot.filters.common.SignalEdgeFilter;
import org.arig.robot.filters.common.SignalEdgeFilter.Type;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

@RunWith(BlockJUnit4ClassRunner.class)
public class SignalEdgeFilterTest {

    private static SignalEdgeFilter risingFilter;
    private static SignalEdgeFilter fallingFilter;
    private static SignalEdgeFilter risingAndFallingFilter;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @BeforeClass
    public static void initClass() {
        risingFilter = new SignalEdgeFilter(false, Type.RISING);
        fallingFilter = new SignalEdgeFilter(false, Type.FALLING);
        risingAndFallingFilter = new SignalEdgeFilter(false, Type.RISING, Type.FALLING);
    }

    @Before
    public void beforeTest() {
        risingFilter.reset();
        fallingFilter.reset();
        risingAndFallingFilter.reset();
    }

    @Test
    public void lastResult() {
        Assert.assertEquals(false, risingFilter.lastResult());
        risingFilter.filter(true);
        Assert.assertEquals(true, risingFilter.lastResult());
    }

    @Test
    public void risingValueIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(IFilter.FILTER_VALUE_NULL_MESSAGE);

        risingFilter.filter(null);
    }

    @Test
    public void testRisingFilter() {
        Assert.assertEquals(false, risingFilter.filter(false));
        Assert.assertEquals(true, risingFilter.filter(true));
        Assert.assertEquals(false, risingFilter.filter(true));
        Assert.assertEquals(false, risingFilter.filter(false));
    }

    @Test
    public void testFallingFilter() {
        Assert.assertEquals(false, fallingFilter.filter(false));
        Assert.assertEquals(false, fallingFilter.filter(true));
        Assert.assertEquals(false, fallingFilter.filter(true));
        Assert.assertEquals(true, fallingFilter.filter(false));
    }

    @Test
    public void testRisingAndFallingFilter() {
        Assert.assertEquals(false, risingAndFallingFilter.filter(false));
        Assert.assertEquals(true, risingAndFallingFilter.filter(true));
        Assert.assertEquals(false, risingAndFallingFilter.filter(true));
        Assert.assertEquals(true, risingAndFallingFilter.filter(false));
    }

    @Test
    public void testReset() {
        risingFilter.filter(true);
        Assert.assertEquals(true, risingFilter.getLastValue());
        risingFilter.reset();
        Assert.assertEquals(false, risingFilter.getLastValue());
    }

    @Test
    public void testLastValue() {
        boolean last = risingFilter.getLastValue();
        boolean value = true;
        for (double i = 0; i < 10; i++) {
            risingFilter.filter(value);
            Assert.assertNotEquals(last, risingFilter.getLastValue());
            Assert.assertEquals(value, risingFilter.getLastValue());
            last = value;
            value = !value;
        }
    }
}
