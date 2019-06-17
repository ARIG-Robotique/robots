package org.arig.test.robot.filters.ramp;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.filters.common.DerivateFilter;
import org.arig.robot.filters.ramp.AbstractRampFilter;
import org.arig.robot.filters.ramp.TrapezoidalRampFilter;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * @author gdepuille on 15/03/15.
 */
@Slf4j
public class TrapezoidalRampFilterTest extends AbstractRampTest {

    @Autowired
    @Qualifier("trapezoidalFilter1")
    private TrapezoidalRampFilter filtre1;

    @Autowired
    @Qualifier("trapezoidalFilter2")
    private TrapezoidalRampFilter filtre2;

    @Override
    protected AbstractRampFilter getFiltre() {
        return filtre1;
    }

    @Test
    public void testProfilMarcheArriereIsoMarcheAvant() {
        filtre1.setConsigneVitesse(200);
        filtre1.setFrein(true);
        filtre2.setConsigneVitesse(200);
        filtre2.setFrein(true);

        double output1 = -1, output2;
        for (int i = 1000 ; output1 != 0 ; i -= output1) {
            output1 = filtre1.filter((long) i);
            output2 = filtre2.filter((long) -i);

            Assert.assertEquals(output1, Math.abs(output2), 0d);
            Assert.assertTrue(output1 >= 0);
            Assert.assertTrue(output2 <= 0);
        }
    }

    @Test
    public void testChangementVitesseAvecFrein() {
        double vitesse = 100;
        double output = -1;

        filtre1.setFrein(true);

        DerivateFilter derivateFilter = new DerivateFilter(0d);
        for (int i = 1000 ; output != 0 ; i -= output) {
            if (i == 905 || i == 505) {
                vitesse = 200;
            } else if (i == 690) {
                vitesse = 100;
            }

            filtre1.setConsigneVitesse(vitesse);
            output = filtre1.filter((long) i);
            double dt = derivateFilter.filter(output);
            log.info("Filtre {} frein actif, Vitesse {}, consigne {}, output {}, dt {}", filtre1.getName(), vitesse, i, output, dt);

            if (i > 945 || (i < 915 && i > 750) || (i < 515 && i > 350)) {
                // Phase d'acceleration
                Assert.assertEquals(1, dt, 0d);
            } else if (i <= 200 || (i < 710 & i > 545)) {
                // Il faut 200 points pour décélerer (vitesse ² / (2 * rampeDecel))
                Assert.assertEquals(-1, dt, 0d);
            } else {
                // Régime établi
                Assert.assertEquals(0, dt, 0d);
            }
        }
    }

    @Test
    public void testChangementVitesseSansFrein() {
        double vitesse = 100;
        double output;

        filtre1.setFrein(false);

        DerivateFilter derivateFilter = new DerivateFilter(0d);
        for (int i = 1000 ; i > 0 ; i -= output) {
            if (i == 905 || i == 505) {
                vitesse = 200;
            } else if (i == 690) {
                vitesse = 100;
            }

            filtre1.setConsigneVitesse(vitesse);
            output = filtre1.filter((long) i);
            double dt = derivateFilter.filter(output);
            log.info("Filtre {} frein inactif, , Vitesse {}, consigne {}, output {}, dt {}", filtre1.getName(), vitesse, i, output, dt);

            if (i > 945 || (i < 915 && i > 750) || (i < 515 && i > 350)) {
                // Phase d'acceleration
                Assert.assertEquals(1, dt, 0d);
            } else if (i < 710 & i > 545) {
                Assert.assertEquals(-1, dt, 0d);
            } else {
                // Régime établi
                Assert.assertEquals(0, dt, 0d);
            }
        }
    }

    @Test
    public void testFilterAvecFrein() {
        double vitesse = 100;
        double output = -1;

        filtre1.setConsigneVitesse(vitesse);
        filtre1.setFrein(true);

        DerivateFilter derivateFilter = new DerivateFilter(0d);
        for (int i = 150 ; output != 0 ; i -= output) {
            output = filtre1.filter((long) i);
            double dt = derivateFilter.filter(output);
            log.info("Filtre {} frein actif, Vitesse {}, consigne {}, output {}, dt {}", filtre1.getName(), vitesse, i, output, dt);

            if (i > 95) {
                // Phase d'acceleration
                Assert.assertEquals(1, dt, 0d);
            } else if (i <= 50) {
                // Il faut 50 points pour décélerer (vitesse ² / (2 * rampeDecel))
                Assert.assertEquals(-1, dt, 0d);
            } else {
                // Régime établi
                Assert.assertEquals(0, dt, 0d);
            }
        }
    }

    @Test
    public void testFilterSansFrein() {
        double vitesse = 100;
        double output;

        filtre1.setConsigneVitesse(vitesse);
        filtre1.setFrein(false);

        DerivateFilter derivateFilter = new DerivateFilter(0d);
        for (int i = 150 ; i > 0 ; i -= output) {
            output = filtre1.filter((long) i);
            double dt = derivateFilter.filter(output);
            log.info("Filtre {} frein inactif, , Vitesse {}, consigne {}, output {}, dt {}", filtre1.getName(), vitesse, i, output, dt);

            if (i > 95) {
                // Phase d'acceleration
                Assert.assertEquals(1, dt, 0d);
            } else {
                // Pas de décelération sur le target quand il n'y a pas de frein
                Assert.assertEquals(0, dt, 0d);
            }
        }
    }
}
