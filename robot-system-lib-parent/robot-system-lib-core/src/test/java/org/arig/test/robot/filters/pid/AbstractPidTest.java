package org.arig.test.robot.filters.pid;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesConfig;
import org.arig.robot.filters.pid.IPidFilter;
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.UUID;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { PidTestContext.class })
public abstract class AbstractPidTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Autowired
    private IMonitoringWrapper monitoringWrapper;

    abstract IPidFilter pid();

    @Before
    public void before() {
        System.setProperty(IConstantesConfig.keyExecutionId, UUID.randomUUID().toString());
        monitoringWrapper.cleanAllPoints();
        pid().reset();
    }

    @After
    public void after() {
        monitoringWrapper.save();
    }

    @Test
    @SneakyThrows
    public void testP() {
        pid().setTunings(1, 0, 0);

        double consigne = 100;
        double input = 0, output, error;
        for (int i = 0 ; i < 100 ; i++) {
            if (i > 10) {
                input = (i * consigne) / 100;
            }
            pid().consigne(consigne);
            error = consigne - input;
            output = pid().filter(input);
            log.info("Test P : consigne {}, mesure {}, output {}", consigne, input, output);
            Assert.assertEquals(error, output, 0);
        }
    }

    @Test
    @SneakyThrows
    public void testPI() {
        pid().setTunings(1, 1, 0);

        double consigne = 100;
        double input = 0, output, error, errorSum = 0;
        for (int i = 0 ; i < 100 ; i++) {
            if (i > 10) {
                input = (i * consigne) / 100;
            }
            pid().consigne(consigne);
            error = consigne - input;
            errorSum += error;
            if (errorSum > 4096) {
                errorSum = 4096;
            }
            output = pid().filter(input);
            log.info("Test PI : consigne {}, mesure {}, output {}", consigne, input, output);

            double expected = error + errorSum;
            Assert.assertEquals(expected, output, 0);
        }
    }

    @Test
    @SneakyThrows
    public void testPID() {
        pid().setTunings(1, 1, 1);

        double consigne = 100;
        double input = 0, output, error, errorSum = 0, errorPrec = 0;
        for (int i = 0 ; i < 100 ; i++) {
            if (i > 10) {
                input = (i * consigne) / 100;
            }
            pid().consigne(consigne);
            error = consigne - input;
            errorSum += error;
            if (errorSum > 4096) {
                errorSum = 4096;
            }
            output = pid().filter(input);
            log.info("Test PID : consigne {}, mesure {}, output {}", consigne, input, output);

            double expected = error + errorSum + error - errorPrec;
            errorPrec = error;
            Assert.assertEquals(expected, output, 0);
        }
    }
}
