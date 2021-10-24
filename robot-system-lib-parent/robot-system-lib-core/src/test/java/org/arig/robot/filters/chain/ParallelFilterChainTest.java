package org.arig.robot.filters.chain;

import org.arig.robot.filters.common.IntegralFilter;
import org.arig.robot.filters.common.ProportionalFilter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class ParallelFilterChainTest {

    private static final double INIT_INTEGRAL = 10;

    private ParallelChainFilter chain;

    private ProportionalFilter f1;
    private ProportionalFilter f2;
    private IntegralFilter f3;

    @BeforeEach
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
        Assertions.assertEquals(16, res, 0);

        // (2 * 1) + (3 * 1) + (11 + 1)
        res = chain.filter(value);
        Assertions.assertEquals(17, res, 0);
    }

    @Test
    public void testReset() {
        final double value = 1;

        double res = chain.filter(value);
        Assertions.assertEquals(16, res, 0);
        Assertions.assertNotEquals(INIT_INTEGRAL, f3.getSum());

        chain.reset();
        Assertions.assertEquals(INIT_INTEGRAL, f3.getSum(), 0);
    }
 }
