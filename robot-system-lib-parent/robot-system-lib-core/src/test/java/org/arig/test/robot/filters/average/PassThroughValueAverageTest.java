package org.arig.test.robot.filters.average;

import org.arig.robot.filters.average.PassThroughValueAverage;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

/**
 * @author gdepuille on 08/05/15.
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class PassThroughValueAverageTest {

    @Test
    public void testAverage() {
        PassThroughValueAverage<Integer> avg = new PassThroughValueAverage<>();

        for (int i = 0 ; i < 20 ; i++) {
            int res = avg.filter(i);
            Assert.assertEquals(i, res);
        }
    }

    @Test
    public void testChangeLimitNoEffect() {
        PassThroughValueAverage<Integer> avg = new PassThroughValueAverage<>();
        Assert.assertEquals(0, avg.size());

        avg.setNbValues(10);
        Assert.assertEquals(0, avg.size());

        avg.reset();
        Assert.assertEquals(0, avg.size());
    }
}
