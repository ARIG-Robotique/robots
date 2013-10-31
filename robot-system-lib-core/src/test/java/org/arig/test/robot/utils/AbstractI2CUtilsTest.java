package org.arig.test.robot.utils;

import lombok.extern.slf4j.Slf4j;

import org.arig.robot.exception.I2CException;
import org.arig.robot.utils.AbstractI2CUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

/**
 * The Class AbstractI2CUtilsTest.
 * 
 * @author mythril
 */
@Slf4j
@RunWith(BlockJUnit4ClassRunner.class)
public class AbstractI2CUtilsTest {

    /** The impl. */
    private static AbstractI2CUtils impl;

    /**
     * Inits the class.
     */
    @BeforeClass
    public static void initClass() {
        AbstractI2CUtilsTest.impl = new AbstractI2CUtils() {
            @Override
            protected void scan() throws I2CException {
                AbstractI2CUtilsTest.log.info("Dummy scan !");
            }

            @Override
            public void reset() throws I2CException {
                getBoardMap().clear();
            }

            @Override
            public boolean isError(final Byte returnCode) {
                // Unix like implementation
                return returnCode != 0;
            }

            @Override
            public void printError(final Byte returnCode) {
                AbstractI2CUtilsTest.log.error("Code d'erreur : " + returnCode);
            }
        };
    }

    /**
     * Inits the test.
     */
    @Before
    public void initTest() {
        for (byte b = 1 ; b < 4 ; b++) {
            AbstractI2CUtilsTest.impl.registerBoard("Board " + b, b);
        }
    }

    /**
     * Test register board1.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testRegisterBoard1() {
        AbstractI2CUtilsTest.impl.registerBoard(null, null);
    }

    /**
     * Test register board2.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testRegisterBoard2() {
        AbstractI2CUtilsTest.impl.registerBoard(null, (byte) 1);
    }

    /**
     * Test register board3.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testRegisterBoard3() {
        AbstractI2CUtilsTest.impl.registerBoard("", (byte) -1);
    }

    /**
     * Test register board.
     */
    @Test
    public void testRegisterBoard() {
        final int init = AbstractI2CUtilsTest.impl.nbBoardRegistered();

        for (byte b = 1 ; b < 4 ; b++) {
            AbstractI2CUtilsTest.impl.registerBoard("Board Bis " + b, b);
        }

        Assert.assertEquals(init + 3, AbstractI2CUtilsTest.impl.nbBoardRegistered());
    }

    /**
     * Test is ok.
     */
    @Test
    public void testIsOk() {
        Assert.assertTrue(AbstractI2CUtilsTest.impl.isOk((byte) 0));
        Assert.assertFalse(AbstractI2CUtilsTest.impl.isOk((byte) 1));
    }

    /**
     * Test execute scan without mapping.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testExecuteScanWithoutMapping() {
        try {
            AbstractI2CUtilsTest.impl.reset();
            AbstractI2CUtilsTest.impl.executeScan();
        } catch (final I2CException e) {
            Assert.fail("Pas la bonne exception : " + e.toString());
        }
    }

    /**
     * Text execute scan.
     *
     * @throws I2CException the i2 c exception
     */
    @Test
    public void textExecuteScan() throws I2CException {
        AbstractI2CUtilsTest.impl.executeScan();
    }

    /**
     * Test get unknown board.
     *
     * @throws I2CException the i2 c exception
     */
    @Test(expected = I2CException.class)
    public void testGetUnknownBoard() throws I2CException {
        AbstractI2CUtilsTest.impl.getBoardAddress("Unknown Board");
    }

    /**
     * Test get known board.
     *
     * @throws I2CException the i2 c exception
     */
    @Test
    public void testGetKnownBoard() throws I2CException {
        Assert.assertEquals(2, AbstractI2CUtilsTest.impl.getBoardAddress("Board 2"), 0);
    }
}
