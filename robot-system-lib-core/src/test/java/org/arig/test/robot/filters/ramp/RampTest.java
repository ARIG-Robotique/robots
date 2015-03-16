package org.arig.test.robot.filters.ramp;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.filters.pid.IPidFilter;
import org.arig.robot.filters.pid.SimplePID;
import org.arig.robot.filters.ramp.IRampFilter;
import org.arig.robot.filters.ramp.Ramp;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by gdepuille on 15/03/15.
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RampTestContext.class})
public class RampTest {

    @Autowired
    private IRampFilter filter;

    @Test
    public void testFilter() {
        double vitesse = 100;
        double output;
        for (int i = 200 ; i > 0 ; i--) {
            if(i == 100) {
                vitesse = 150;
            }
            output = filter.filter(vitesse, i, 0, true);
            log.info("Vitesse {}, consigne {}, output {}", vitesse, i, output);
        }
    }
}
