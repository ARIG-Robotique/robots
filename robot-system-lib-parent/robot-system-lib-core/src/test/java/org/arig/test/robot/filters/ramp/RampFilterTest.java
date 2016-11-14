package org.arig.test.robot.filters.ramp;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesConfig;
import org.arig.robot.filters.ramp.IRampFilter;
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.junit.After;
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
@ContextConfiguration(classes = {RampTestContext.class})
public class RampFilterTest {

    @Autowired
    private IRampFilter filter;

    @Autowired
    private IMonitoringWrapper monitoringWrapper;

    @Before
    public void before() {
        System.setProperty(IConstantesConfig.keyExecutionId, UUID.randomUUID().toString());
        monitoringWrapper.clean();
    }

    @After
    public void after() {
        monitoringWrapper.save();
    }

    @Test
    public void testFilter() throws Exception {
        double vitesse = 100;
        double output;
        for (int i = 200 ; i >= -200 ; i--) {
            if(i == 100) {
                vitesse = 150;
            }
            if (i == -100) {
                vitesse = 100;
            }
            output = filter.filter(vitesse, i, true);
            log.info("Vitesse {}, consigne {}, output {}", vitesse, i, output);
        }
    }
}
