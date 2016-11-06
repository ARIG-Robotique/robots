package org.arig.test.robot.filters.values;

import org.arig.robot.filters.values.MovingIntegerValueAverage;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

/**
 * @author gdepuille on 08/05/15.
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class MovingIntegerValueAverageTest {

    @Test
    public void testAverage() {
        MovingIntegerValueAverage avg = new MovingIntegerValueAverage();

        int calc = 0;
        for (int i = 0 ; i < 20 ; i++) {
            int res = avg.average(i);
            calc = (calc + i) / 2;
            Assert.assertEquals(calc, res);
        }

        avg.reset();
        avg.average(calc = 0);
        for (int i = 20 ; i > 0 ; i--) {
            int res = avg.average(i);
            calc = (calc + i) / 2;
            Assert.assertTrue(calc - res <= 1);
        }
    }

    @Test
    public void testChangeLimit() {
        int limit = 5;
        MovingIntegerValueAverage avg = new MovingIntegerValueAverage(limit);
        for (int i = 0 ; i < limit * 2 ; i++) {
            avg.average(i);
            Assert.assertEquals(Math.min(i + 1, limit), avg.size());
        }
        Assert.assertEquals(limit, avg.size());

        int limit2 = 10;
        avg.setLimit(limit2);
        for (int i = 0 ; i < limit2 * 2 ; i++) {
            avg.average(i);
            Assert.assertEquals(Math.min(i + limit + 1, limit2), avg.size());
        }
        Assert.assertEquals(limit2, avg.size());
    }

    @Test
    public void testReset() {
        int limit = 5, nbExec = 0;
        MovingIntegerValueAverage avg = new MovingIntegerValueAverage(limit);
        do {
            for (int i = 0; i < limit * 2; i++) {
                avg.average(i);
                Assert.assertEquals(Math.min(i + 1, limit), avg.size());
            }
            Assert.assertEquals(limit, avg.size());

            avg.reset();
            Assert.assertEquals(0, avg.size());

            nbExec++;
        } while(nbExec < 3);
    }
}
