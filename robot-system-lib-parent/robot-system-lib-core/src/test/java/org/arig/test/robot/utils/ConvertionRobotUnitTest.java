package org.arig.test.robot.utils;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

/**
 * The Class ConvertionRobotUnitTest.
 * 
 * @author gdepuille
 */
@Slf4j
@RunWith(BlockJUnit4ClassRunner.class)
public class ConvertionRobotUnitTest {

    /** The conv1. */
    private static ConvertionRobotUnit conv1;

    /** The conv0_5. */
    private static ConvertionRobotUnit conv0_5;

    /**
     * Inits the class.
     */
    @BeforeClass
    public static void initClass() {
        ConvertionRobotUnitTest.conv1 = new ConvertionRobotUnit(1, 1);
        ConvertionRobotUnitTest.conv0_5 = new ConvertionRobotUnit(0.5, 0.5);
    }

    /**
     * Test pi value.
     */
    @Test
    public void testPiValue() {
        log.info("Test des valeurs de PI en pulse");

        Assert.assertEquals(180, ConvertionRobotUnitTest.conv1.getPiPulse(), 0);
        Assert.assertEquals(360, ConvertionRobotUnitTest.conv1.getPi2Pulse(), 0);
    }

    /**
     * Test mm convertion.
     */
    @Test
    public void testMmConvertion() {
        log.info("Test des convertions en millimètres");

        Assert.assertEquals(1, ConvertionRobotUnitTest.conv1.mmToPulse(1), 0);
        Assert.assertEquals(0.5, ConvertionRobotUnitTest.conv0_5.mmToPulse(1), 0);

        Assert.assertEquals(1, ConvertionRobotUnitTest.conv1.pulseToMm(1), 0);
        Assert.assertEquals(2, ConvertionRobotUnitTest.conv0_5.pulseToMm(1), 0);
    }

    /**
     * Test deg convertion.
     */
    @Test
    public void testDegConvertion() {
        log.info("Test des convertions en degrées");

        Assert.assertEquals(1, ConvertionRobotUnitTest.conv1.degToPulse(1), 0);
        Assert.assertEquals(0.5, ConvertionRobotUnitTest.conv0_5.degToPulse(1), 0);

        Assert.assertEquals(1, ConvertionRobotUnitTest.conv1.pulseToDeg(1), 0);
        Assert.assertEquals(2, ConvertionRobotUnitTest.conv0_5.pulseToDeg(1), 0);
    }

    /**
     * Test rad convertion.
     */
    @Test
    public void testRadConvertion() {
        log.info("Test des convertions en radians");

        Assert.assertEquals(Math.toDegrees(1), ConvertionRobotUnitTest.conv1.radToPulse(1), 0);
        Assert.assertEquals(Math.toDegrees(0.5), ConvertionRobotUnitTest.conv0_5.radToPulse(1), 0);

        Assert.assertEquals(Math.toRadians(1), ConvertionRobotUnitTest.conv1.pulseToRad(1), 0);
        Assert.assertEquals(Math.toRadians(2), ConvertionRobotUnitTest.conv0_5.pulseToRad(1), 0);
    }
}
