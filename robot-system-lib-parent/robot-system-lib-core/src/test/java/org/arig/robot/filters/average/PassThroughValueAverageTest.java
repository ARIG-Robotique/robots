package org.arig.robot.filters.average;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * @author gdepuille on 08/05/15.
 */
@ExtendWith(SpringExtension.class)
public class PassThroughValueAverageTest {

    @Test
    public void testAverage() {
        PassThroughValueAverage<Integer> avg = new PassThroughValueAverage<>();

        for (int i = 0 ; i < 20 ; i++) {
            int res = avg.filter(i);
            Assertions.assertEquals(i, res);
        }
    }

    @Test
    public void testChangeLimitNoEffect() {
        PassThroughValueAverage<Integer> avg = new PassThroughValueAverage<>();
        Assertions.assertEquals(0, avg.size());

        avg.setNbValues(10);
        Assertions.assertEquals(0, avg.size());

        avg.reset();
        Assertions.assertEquals(0, avg.size());
    }
}
