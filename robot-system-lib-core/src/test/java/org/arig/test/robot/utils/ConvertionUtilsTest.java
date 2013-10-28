package org.arig.test.robot.utils;

import lombok.extern.slf4j.Slf4j;

import org.arig.robot.utils.ConvertionUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

/**
 * The Class ConvertionUtilsTest.
 * 
 * @author mythril
 */
@Slf4j
@RunWith(BlockJUnit4ClassRunner.class)
public class ConvertionUtilsTest {

	/** The conv1. */
	private static ConvertionUtils conv1;

	/** The conv0_5. */
	private static ConvertionUtils conv0_5;

	/**
	 * Inits the class.
	 */
	@BeforeClass
	public static void initClass() {
		ConvertionUtilsTest.conv1 = new ConvertionUtils(1, 1);
		ConvertionUtilsTest.conv0_5 = new ConvertionUtils(0.5, 0.5);
	}

	/**
	 * Test pi value.
	 */
	@Test
	public void testPiValue() {
		ConvertionUtilsTest.log.info("Test des valeurs de PI en pulse");

		Assert.assertEquals(180, ConvertionUtilsTest.conv1.getPiPulse(), 0);
		Assert.assertEquals(360, ConvertionUtilsTest.conv1.getPi2Pulse(), 0);
	}

	/**
	 * Test mm convertion.
	 */
	@Test
	public void testMmConvertion() {
		ConvertionUtilsTest.log.info("Test des convertions en millimètres");

		Assert.assertEquals(1, ConvertionUtilsTest.conv1.mmToPulse(1), 0);
		Assert.assertEquals(0.5, ConvertionUtilsTest.conv0_5.mmToPulse(1), 0);

		Assert.assertEquals(1, ConvertionUtilsTest.conv1.pulseToMm(1), 0);
		Assert.assertEquals(2, ConvertionUtilsTest.conv0_5.pulseToMm(1), 0);
	}

	/**
	 * Test deg convertion.
	 */
	@Test
	public void testDegConvertion() {
		ConvertionUtilsTest.log.info("Test des convertions en degrées");

		Assert.assertEquals(1, ConvertionUtilsTest.conv1.degToPulse(1), 0);
		Assert.assertEquals(0.5, ConvertionUtilsTest.conv0_5.degToPulse(1), 0);

		Assert.assertEquals(1, ConvertionUtilsTest.conv1.pulseToDeg(1), 0);
		Assert.assertEquals(2, ConvertionUtilsTest.conv0_5.pulseToDeg(1), 0);
	}

	/**
	 * Test rad convertion.
	 */
	@Test
	public void testRadConvertion() {
		ConvertionUtilsTest.log.info("Test des convertions en radians");

		Assert.assertEquals(Math.toDegrees(1), ConvertionUtilsTest.conv1.radToPulse(1), 0);
		Assert.assertEquals(Math.toDegrees(0.5), ConvertionUtilsTest.conv0_5.radToPulse(1), 0);

		Assert.assertEquals(Math.toRadians(1), ConvertionUtilsTest.conv1.pulseToRad(1), 0);
		Assert.assertEquals(Math.toRadians(2), ConvertionUtilsTest.conv0_5.pulseToRad(1), 0);
	}
}
