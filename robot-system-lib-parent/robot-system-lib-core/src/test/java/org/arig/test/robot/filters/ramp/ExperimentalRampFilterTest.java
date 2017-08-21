package org.arig.test.robot.filters.ramp;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesConfig;
import org.arig.robot.filters.common.DerivateFilter;
import org.arig.robot.filters.ramp.experimental.ExperimentalRampFilter;
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author gdepuille on 11/11/2017.
 */
@Slf4j
public class ExperimentalRampFilterTest extends AbstractRampTest {

    @Getter
    @Autowired
    private ExperimentalRampFilter filtre;

    @Test
    public void testChangementVitesse() {
        double vitesse = 100;
        double output;

        DerivateFilter derivateFilter = new DerivateFilter(0d);
        for (int i = 1000 ; i > 0 ; i -= output) {
            if (i == 905 || i == 505) {
                vitesse = 200;
            } else if (i == 690) {
                vitesse = 100;
            }

            filtre.setConsigneVitesse(vitesse);
            output = filtre.filter((long) i);
            double dt = derivateFilter.filter(output);
            log.info("Filtre {}, Vitesse {}, consigne {}, output {}, dt {}", filtre.getName(), vitesse, i, output, dt);

            /*if (i > 945 || (i < 915 && i > 750) || (i < 515 && i > 350)) {
                // Phase d'acceleration
                Assert.assertEquals(1, dt, 0d);
            } else if (i <= 200 || (i < 710 & i > 545)) {
                // Il faut 200 points pour décélerer (vitesse ² / (2 * rampeDecel))
                Assert.assertEquals(-1, dt, 0d);
            } else {
                // Régime établi
                Assert.assertEquals(0, dt, 0d);
            }*/
        }
    }

    @Test
    public void testFilter() {
        double vitesse = 100;
        double output;

        filtre.setConsigneVitesse(vitesse);

        DerivateFilter derivateFilter = new DerivateFilter(0d);
        for (int i = 150 ; i > 100 ; i -= output) {
            output = filtre.filter((long) i);
            double dt = derivateFilter.filter(output);
            log.info("Filtre {}, Vitesse {}, consigne {}, output {}, dt {}", filtre.getName(), vitesse, i, output, dt);

            /*
            if (i > 95) {
                // Phase d'acceleration
                Assert.assertEquals(1, dt, 0d);
            } else if (i <= 50) {
                // Il faut 50 points pour décélerer (vitesse ² / (2 * rampeDecel))
                Assert.assertEquals(-1, dt, 0d);
            } else {
                // Régime établi
                Assert.assertEquals(0, dt, 0d);
            }*/
        }
    }
}
