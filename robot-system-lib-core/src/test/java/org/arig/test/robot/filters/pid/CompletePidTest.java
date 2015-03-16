package org.arig.test.robot.filters.pid;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.filters.pid.CompletePID;
import org.arig.robot.filters.pid.IPidFilter;
import org.arig.robot.filters.pid.SimplePID;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by gdepuille on 15/03/15.
 */
@Slf4j
@RunWith(BlockJUnit4ClassRunner.class)
public class CompletePidTest {

    private static IPidFilter pid;

    @BeforeClass
    public static void initClass() {
        CompletePID pid = new CompletePID();
        pid.setControllerDirection(IPidFilter.PidType.DIRECT);
        pid.setMode(IPidFilter.PidMode.AUTOMATIC);
        pid.setSampleTime(1);
        pid.reset();
        pid.initialise();

        CompletePidTest.pid = pid;
    }

    @Test
    public void testP() {
        pid.setTunings(1, 0, 0);

        double consigne = 100;
        double input = 0, output = 0;
        for (int i = 0 ; i < 100 ; i++) {
            if (i > 10) {
                input = (i * consigne) / 100;
            }
            output = pid.compute(consigne, input);
            log.info("Test P : consigne {}, input {}, output {}", consigne, input, output);
            Assert.assertEquals(consigne - input, output, 1);
        }
    }
}
