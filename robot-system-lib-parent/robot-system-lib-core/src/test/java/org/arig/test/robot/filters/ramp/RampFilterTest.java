package org.arig.test.robot.filters.ramp;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.filters.ramp.IRampFilter;
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
@ContextConfiguration(classes = {RampTestContext.class})
public class RampFilterTest {

    @Autowired
    private IRampFilter filter;

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
