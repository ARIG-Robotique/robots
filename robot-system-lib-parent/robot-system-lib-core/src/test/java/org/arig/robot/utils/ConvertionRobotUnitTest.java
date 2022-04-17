package org.arig.robot.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * The Class ConvertionRobotUnitTest.
 *
 * @author gdepuille
 */
@Slf4j
@ExtendWith(SpringExtension.class)
public class ConvertionRobotUnitTest {

    private static ConvertionRobotUnit conv1;
    private static ConvertionRobotUnit conv0_5;

    @BeforeAll
    public static void initClass() {
        ConvertionRobotUnitTest.conv1 = new ConvertionRobotUnit(1, 1);
        ConvertionRobotUnitTest.conv0_5 = new ConvertionRobotUnit(0.5, 0.5);
    }

    @Test
    void testEntraxe() {
        ConvertionRobotUnit conv = new ConvertionRobotUnit(27.74036795337890000, 280, true);

        Assertions.assertEquals(27.74036795337890000, conv.countPerMm(), 0);
        Assertions.assertEquals(280, conv.entraxe(), 0);
        Assertions.assertEquals(135.56501182033114, conv.countPerDegree(), 0);
    }

    @Test
    void testPiValue() {
        log.info("Test des valeurs de PI en pulse");

        Assertions.assertEquals(180, ConvertionRobotUnitTest.conv1.piPulse(), 0);
        Assertions.assertEquals(360, ConvertionRobotUnitTest.conv1.pi2Pulse(), 0);
    }

    @Test
    void testMmConvertion() {
        log.info("Test des convertions en millimètres");

        Assertions.assertEquals(1, ConvertionRobotUnitTest.conv1.mmToPulse(1), 0);
        Assertions.assertEquals(0.5, ConvertionRobotUnitTest.conv0_5.mmToPulse(1), 0);

        Assertions.assertEquals(1, ConvertionRobotUnitTest.conv1.pulseToMm(1), 0);
        Assertions.assertEquals(2, ConvertionRobotUnitTest.conv0_5.pulseToMm(1), 0);
    }

    @Test
    void testDegConvertion() {
        log.info("Test des convertions en degrées");

        Assertions.assertEquals(1, ConvertionRobotUnitTest.conv1.degToPulse(1), 0);
        Assertions.assertEquals(0.5, ConvertionRobotUnitTest.conv0_5.degToPulse(1), 0);

        Assertions.assertEquals(1, ConvertionRobotUnitTest.conv1.pulseToDeg(1), 0);
        Assertions.assertEquals(2, ConvertionRobotUnitTest.conv0_5.pulseToDeg(1), 0);
    }

    @Test
    void testRadConvertion() {
        log.info("Test des convertions en radians");

        Assertions.assertEquals(Math.toDegrees(1), ConvertionRobotUnitTest.conv1.radToPulse(1), 0);
        Assertions.assertEquals(Math.toDegrees(0.5), ConvertionRobotUnitTest.conv0_5.radToPulse(1), 0);

        Assertions.assertEquals(Math.toRadians(1), ConvertionRobotUnitTest.conv1.pulseToRad(1), 0);
        Assertions.assertEquals(Math.toRadians(2), ConvertionRobotUnitTest.conv0_5.pulseToRad(1), 0);
    }
}
