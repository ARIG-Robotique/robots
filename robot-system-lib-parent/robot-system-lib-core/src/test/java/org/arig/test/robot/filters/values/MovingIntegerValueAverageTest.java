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
}
