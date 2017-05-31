package org.arig.test.robot.filters.pid;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesConfig;
import org.arig.robot.filters.pid.SimplePidFilter;
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
public class SimplePidTest {

    @Autowired
    private SimplePidFilter pid;

    @Autowired
    private IMonitoringWrapper monitoringWrapper;

    @Before
    public void before() {
        System.setProperty(IConstantesConfig.keyExecutionId, UUID.randomUUID().toString());
        monitoringWrapper.cleanAllPoints();
        pid.reset();
    }

    @After
    public void after() {
        monitoringWrapper.save();
    }

    @Test
    @SneakyThrows
    public void testP() {
        double consigne = 100;
        double input = 0, output;
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
}
