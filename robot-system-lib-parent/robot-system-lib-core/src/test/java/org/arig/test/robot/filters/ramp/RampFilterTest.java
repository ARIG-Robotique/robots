package org.arig.test.robot.filters.ramp;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesConfig;
import org.arig.robot.filters.ramp.IRampFilter;
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author gdepuille on 15/03/15.
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RampTestContext.class})
public class RampFilterTest {

    @Autowired
    @Qualifier("filter1")
    private IRampFilter filter1;

    @Autowired
    @Qualifier("filter2")
    private IRampFilter filter2;

    @Autowired
    private IMonitoringWrapper monitoringWrapper;

    @Before
    public void before() {
        System.setProperty(IConstantesConfig.keyExecutionId, UUID.randomUUID().toString());
        monitoringWrapper.clean();

        filter1.reset();
        filter2.reset();
    }

    @After
    public void after() {
        monitoringWrapper.save();
    }

    @Test
    @DirtiesContext
    public void testChangeSampleTime() {
        filter1.setSampleTime(1000);
        Assert.assertEquals(1, filter1.getSampleTimeS(), 0);

        filter1.setSampleTime(1500);
        Assert.assertEquals(1.5, filter1.getSampleTimeS(), 0);

        filter1.setSampleTime(10, TimeUnit.SECONDS);
        Assert.assertEquals(10, filter1.getSampleTimeS(), 0);
    }

    @Test
    public void testFilterWithFrein() {
        testFilter(true, false);
    }

    @Test
    public void testFilterWithoutFrein() {
        testFilter(false, false);
    }

    @Test
    public void testSurchargeFilterHasSameProcess() {
        for (int i = 200 ; i >= 0 ; i--) {
            Assert.assertEquals(
                    filter1.filter(100, i, true),
                    filter2.filter(100, i, true, false),
                    0
            );
        }
    }

    private void testFilter(boolean frein, boolean bypass) {
        double vitesse = 100;
        double output;
        for (int i = 200 ; i >= -200 ; i--) {
            if(i == 100) {
                vitesse = 150;
            }
            if (i == -100) {
                vitesse = 100;
            }
            output = filter1.filter(vitesse, i, frein, bypass);
            log.info("Frein {}, Bypass {}, Vitesse {}, consigne {}, output {}", frein, bypass, vitesse, i, output);
        }
    }
}
