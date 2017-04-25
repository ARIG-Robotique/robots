package org.arig.test.robot.filters.pid;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesConfig;
import org.arig.robot.filters.pid.CompletePidFilter;
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.UUID;

/**
 * @author gdepuille on 15/03/15.
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { PidTestContext.class })
public class CompletePidTest {

    @Autowired
    private CompletePidFilter pid;

    @Autowired
    private IMonitoringWrapper monitoringWrapper;

    @Before
    public void before() {
        System.setProperty(IConstantesConfig.keyExecutionId, UUID.randomUUID().toString());
        monitoringWrapper.clean();
        pid.reset();
    }

    @After
    public void after() {
        monitoringWrapper.save();
    }

    @Test
    @SneakyThrows
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

            Thread.sleep(1);
        }
    }

    @Test
    @SneakyThrows
    public void testPI() {
        pid.setTunings(1, 1, 0);

        double consigne = 100;
        double input = 0, output = 0;
        for (int i = 0 ; i < 100 ; i++) {
            if (i > 10) {
                input = (i * consigne) / 100;
            }
            output = pid.compute(consigne, input);
            log.info("Test P : consigne {}, input {}, output {}", consigne, input, output);

            Thread.sleep(1);
        }
    }

    @Test
    @SneakyThrows
    public void testPID() {
        pid.setTunings(1, 1, 1);

        double consigne = 100;
        double input = 0, output;
        for (int i = 0 ; i < 100 ; i++) {
            if (i > 10) {
                input = (i * consigne) / 100;
            }
            output = pid.compute(consigne, input);
            log.info("Test P : consigne {}, input {}, output {}", consigne, input, output);

            Thread.sleep(1);
        }
    }
}
