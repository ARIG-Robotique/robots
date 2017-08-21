package org.arig.test.robot.filters.chain;

import org.arig.robot.filters.chain.ParallelChainFilter;
import org.arig.robot.filters.chain.SerialChainFilter;
import org.arig.robot.filters.common.IntegralFilter;
import org.arig.robot.filters.common.ProportionalFilter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

@RunWith(BlockJUnit4ClassRunner.class)
public class ParallelFilterChainTest {

    private static final double INIT_INTEGRAL = 10;

    private ParallelChainFilter chain;

    private ProportionalFilter f1;
    private ProportionalFilter f2;
    private IntegralFilter f3;

    @Before
    public void init() {
        f1 = new ProportionalFilter(2d);
        f2 = new ProportionalFilter(3d);
        f3 = new IntegralFilter(INIT_INTEGRAL);

        chain = new ParallelChainFilter();
        chain.addFilter(f1);
        chain.addFilter(f2);
        chain.addFilter(f3);
    }

    @Test
    public void testFilter() {
        final double value = 1;

        // (2 * 1) + (3 * 1) + (10 + 1)
        double res = chain.filter(value);
        Assert.assertEquals(16, res, 0);

        // (2 * 1) + (3 * 1) + (11 + 1)
        res = chain.filter(value);
        Assert.assertEquals(17, res, 0);
    }

    @Test
    public void testReset() {
        final double value = 1;

        double res = chain.filter(value);
        Assert.assertEquals(16, res, 0);
        Assert.assertNotEquals(INIT_INTEGRAL, f3.getSum());

        chain.reset();
        Assert.assertEquals(INIT_INTEGRAL, f3.getSum(), 0);
    }
 }
