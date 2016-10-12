package org.arig.test.robot.filters.pid;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.filters.pid.SimplePidFilter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by gdepuille on 15/03/15.
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { PidContext.class })
public class SimplePidTest {

    @Autowired
    private SimplePidFilter pid;

    @Test
    @SneakyThrows
    public void testP() {
        double consigne = 100;
        double input = 0, output = 0;
        for (int i = 0 ; i < 100 ; i++) {
            if (i > 10) {
                input = (i * consigne) / 100;
            }
            output = pid.compute(consigne, input);
            log.info("Test P : consigne {}, input {}, output {}", consigne, input, output);
            Assert.assertEquals(consigne - input, output, 1);

            Thread.currentThread().sleep(10);
        }
    }
}
