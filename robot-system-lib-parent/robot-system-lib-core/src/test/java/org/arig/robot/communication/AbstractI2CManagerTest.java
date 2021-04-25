package org.arig.robot.communication;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.I2CManagerDevice.I2CManagerDeviceBuilder;
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
        impl = new DummyI2CManager();
    }

    @Before
    @SneakyThrows
    public void initTest() {
        impl.reset();

        final I2CManagerDevice<Byte> mux = I2CManagerDevice.<Byte>builder()
                .device((byte) 0)
                .deviceName("Board Multiplexeur")
                .build();
        impl.registerDevice(mux);
        impl.registerMultiplexerDevice(mux.deviceName(), new II2CMultiplexerDevice() {
            public boolean selectChannel(final byte channel) {
                log.info("Selection du canal {}", channel);
                return true;
            }

            public void disable() {
                log.info("Désactivation du multiplexeur");
            }
        });

        for (byte b = 1; b < 6; b++) {
            final I2CManagerDeviceBuilder<Byte> builder = I2CManagerDevice.<Byte>builder()
                    .device(b)
                    .deviceName("Board " + b);

            if (b >= 4) {
                builder.multiplexerChannel((byte) (b - 3)).multiplexerDeviceName(mux.deviceName());
            }

            final I2CManagerDevice<Byte> d = builder.build();
            impl.registerDevice(d);
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testAlreadyRegisteredBoard() {
        impl.registerDevice(impl.getDeviceMap().values().iterator().next());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterBoardNull() {
        impl.registerDevice(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterBoardNullDeviceName() {
        I2CManagerDevice<Byte> d = I2CManagerDevice.<Byte>builder().device((byte) 1).build();
        impl.registerDevice(d);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterBoardEmptyDeviceName() {
        I2CManagerDevice<Byte> d = I2CManagerDevice.<Byte>builder().device((byte) -1).deviceName("").build();
        impl.registerDevice(d);
    }

    @Test
    public void testRegisterBoard() {
        final int init = impl.nbDeviceRegistered();

        for (byte b = 1; b < 4; b++) {
            I2CManagerDevice<Byte> d = I2CManagerDevice.<Byte>builder()
                    .device(b)
                    .deviceName("Board Bis" + b)
                    .build();
            impl.registerDevice(d);
        }

        Assert.assertEquals(init + 3, impl.nbDeviceRegistered());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterMultiplexedBoardWithoutChannel() {
        impl.registerDevice(I2CManagerDevice.<Byte>builder()
                .device((byte) 2)
                .deviceName("Board multiplexe sans channel")
                .multiplexerDeviceName("Board multiplexer")
                .build()
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExecuteScanWithoutMapping() {
        try {
            impl.reset();
            impl.executeScan();
        } catch (final I2CException e) {
            Assert.fail("Pas la bonne exception : " + e);
        }
    }

    @Test
    public void textExecuteScan() throws I2CException {
        impl.executeScan();
        Assert.assertTrue(impl.status());
    }

    @Test(expected = I2CException.class)
    public void testGetUnknownBoard() throws I2CException {
        impl.getDevice("Unknown Board");
    }

    @Test
    public void testGetKnownBoard() throws I2CException {
        I2CManagerDevice<Byte> d = impl.getDevice("Board 2");
        Assert.assertEquals(2, d.device(), 0);
    }
}
