package org.arig.test.robot.filters.values;

import org.arig.robot.filters.values.PassThroughValueAverage;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

/**
 * Created by gdepuille on 08/05/15.
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class PassThroughValueAverageTest {

    @Test
    public void testAverage() {
        PassThroughValueAverage<Integer> avg = new PassThroughValueAverage();

        for (int i = 0 ; i < 20 ; i++) {
            int res = avg.average(i);
            Assert.assertEquals(i, res);
        }
    }
}
