package org.arig.test.robot.communication;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.AbstractI2CManager;
import org.arig.robot.exception.I2CException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

/**
 * The Class AbstractI2CManagerTest.
 * 
 * @author mythril
 */
@Slf4j
@RunWith(BlockJUnit4ClassRunner.class)
public class AbstractI2CManagerTest {

    /** The impl. */
    private static AbstractI2CManager<Byte> impl;

    /**
     * Inits the class.
     */
    @BeforeClass
    public static void initClass() {
        AbstractI2CManagerTest.impl = new AbstractI2CManager<Byte>() {

            @Override
            protected void scan() throws I2CException {
                AbstractI2CManagerTest.log.info("Dummy scan !");
            }

            @Override
            public void sendData(String deviceName, byte... datas) {}

            @Override
            public void sendData(String deviceName, int nbResult, byte... datas) {}

            @Override
            public byte getData(String deviceName) {
                return 127;
            }

            @Override
            public byte[] getDatas(String deviceName, int size) {
                return new byte[] {12, 32, 45};
            }
        };
    }

    /**
     * Inits the test.
     */
    @Before
    public void initTest() {
        for (byte b = 1; b < 4; b++) {
            AbstractI2CManagerTest.impl.registerDevice("Board " + b, b);
        }
    }

    /**
     * Test register board1.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testRegisterBoard1() {
        AbstractI2CManagerTest.impl.registerDevice(null, null);
    }

    /**
     * Test register board2.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testRegisterBoard2() {
        AbstractI2CManagerTest.impl.registerDevice(null, (byte) 1);
    }

    /**
     * Test register board3.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testRegisterBoard3() {
        AbstractI2CManagerTest.impl.registerDevice("", (byte) -1);
    }

    /**
     * Test register board.
     */
    @Test
    public void testRegisterBoard() {
        final int init = AbstractI2CManagerTest.impl.nbDeviceRegistered();

        for (byte b = 1; b < 4; b++) {
            AbstractI2CManagerTest.impl.registerDevice("Board Bis " + b, b);
        }

        Assert.assertEquals(init + 3, AbstractI2CManagerTest.impl.nbDeviceRegistered());
    }

    /**
     * Test execute scan without mapping.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testExecuteScanWithoutMapping() {
        try {
            AbstractI2CManagerTest.impl.reset();
            AbstractI2CManagerTest.impl.executeScan();
        } catch (final I2CException e) {
            Assert.fail("Pas la bonne exception : " + e.toString());
        }
    }

    /**
     * Text execute scan.
     * 
     * @throws I2CException
     *             the i2 c exception
     */
    @Test
    public void textExecuteScan() throws I2CException {
        AbstractI2CManagerTest.impl.executeScan();
    }

    /**
     * Test get unknown board.
     * 
     * @throws I2CException
     *             the i2 c exception
     */
    @Test(expected = I2CException.class)
    public void testGetUnknownBoard() throws I2CException {
        AbstractI2CManagerTest.impl.getDevice("Unknown Board");
    }

    /**
     * Test get known board.
     * 
     * @throws I2CException
     *             the i2 c exception
     */
    @Test
    public void testGetKnownBoard() throws I2CException {
        Assert.assertEquals(2, (byte) AbstractI2CManagerTest.impl.getDevice("Board 2"), 0);
    }
}
