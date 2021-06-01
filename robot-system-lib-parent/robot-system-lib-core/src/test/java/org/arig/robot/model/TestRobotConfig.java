package org.arig.robot.model;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

@Slf4j
@RunWith(BlockJUnit4ClassRunner.class)
public class TestRobotConfig {

    private static RobotConfig rc = new RobotConfig();

    @BeforeClass
    public static void initClass() {
        rc.vitesseMin(100);
        rc.vitesseOrientationMin(100);
        rc.vitesseMax(1000);
        rc.vitesseOrientationMax(1000);
        rc.vitesseDefRatio(10);
    }

    @Test
    public void testVitesse() {
        log.info("Test des valeurs ratio vitesse");

        for (int i = 0 ; i <= 100 ; i++) {
            log.info("PCT : {} : {} distance, {} orientation", i, rc.vitesse(i), rc.vitesseOrientation(i));
            Assert.assertEquals(rc.vitesse(i), rc.vitesseOrientation(i));
        }
    }
}
