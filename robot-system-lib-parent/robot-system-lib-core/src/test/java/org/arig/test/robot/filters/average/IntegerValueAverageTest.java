package org.arig.test.robot.filters.average;

import org.arig.robot.filters.average.IntegerValueAverage;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

/**
 * @author gdepuille on 08/05/15.
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class IntegerValueAverageTest {

    @Test
    public void testAverage() {
        IntegerValueAverage avg = new IntegerValueAverage();

        int expected = 0;
        for (int i = 0 ; i < 20 ; i++) {
            int res = avg.filter(i);
            expected = (expected + i) / 2;
            Assert.assertEquals(expected, res);
        }

        avg.reset();
        avg.filter(expected = 0);
        for (int i = 20 ; i > 0 ; i--) {
            int res = avg.filter(i);
            expected = (expected + i) / 2;
            Assert.assertTrue(expected - res <= 1);
        }
    }

    @Test
    public void testChangeLimit() {
        int limit = 5;
        IntegerValueAverage avg = new IntegerValueAverage(limit);
        for (int i = 0 ; i < limit * 2 ; i++) {
            avg.filter(i);
            Assert.assertEquals(Math.min(i + 1, limit), avg.size());
        }
        Assert.assertEquals(limit, avg.size());

        int limit2 = 10;
        avg.setNbValues(limit2);
        for (int i = 0 ; i < limit2 * 2 ; i++) {
            avg.filter(i);
            Assert.assertEquals(Math.min(i + limit + 1, limit2), avg.size());
        }
        Assert.assertEquals(limit2, avg.size());
    }

    @Test
    public void testReset() {
        int limit = 5, nbExec = 0;
        IntegerValueAverage avg = new IntegerValueAverage(limit);
        do {
            for (int i = 0; i < limit * 2; i++) {
                avg.filter(i);
                Assert.assertEquals(Math.min(i + 1, limit), avg.size());
            }
            Assert.assertEquals(limit, avg.size());

            avg.reset();
            Assert.assertEquals(0, avg.size());

            nbExec++;
        } while(nbExec < 3);
    }
}
