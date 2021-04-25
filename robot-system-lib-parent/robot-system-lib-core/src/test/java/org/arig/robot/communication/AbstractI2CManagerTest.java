package org.arig.robot.communication;

import lombok.extern.slf4j.Slf4j;
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
 * @author gdepuille
 */
@Slf4j
@RunWith(BlockJUnit4ClassRunner.class)
public class AbstractI2CManagerTest {

    private static AbstractI2CManager<Byte> impl;

    @BeforeClass
    public static void initClass() {
        AbstractI2CManagerTest.impl = new DummyI2CManager();
    }

    @Before
    public void initTest() {
        for (byte b = 1; b < 4; b++) {
            AbstractI2CManagerTest.impl.registerDevice("Board " + b, b);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterBoard1() {
        AbstractI2CManagerTest.impl.registerDevice(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterBoard2() {
        AbstractI2CManagerTest.impl.registerDevice(null, (byte) 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterBoard3() {
        AbstractI2CManagerTest.impl.registerDevice("", (byte) -1);
    }

    @Test
    public void testRegisterBoard() {
        final int init = AbstractI2CManagerTest.impl.nbDeviceRegistered();

        for (byte b = 1; b < 4; b++) {
            AbstractI2CManagerTest.impl.registerDevice("Board Bis " + b, b);
        }

        Assert.assertEquals(init + 3, AbstractI2CManagerTest.impl.nbDeviceRegistered());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExecuteScanWithoutMapping() {
        try {
            AbstractI2CManagerTest.impl.reset();
            AbstractI2CManagerTest.impl.executeScan();
        } catch (final I2CException e) {
            Assert.fail("Pas la bonne exception : " + e.toString());
        }
    }

    @Test
    public void textExecuteScan() throws I2CException {
        AbstractI2CManagerTest.impl.executeScan();
    }

    @Test(expected = I2CException.class)
    public void testGetUnknownBoard() throws I2CException {
        AbstractI2CManagerTest.impl.getDevice("Unknown Board");
    }

    @Test
    public void testGetKnownBoard() throws I2CException {
        Assert.assertEquals(2, AbstractI2CManagerTest.impl.getDevice("Board 2"), 0);
    }
}
