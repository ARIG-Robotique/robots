package org.arig.robot.communication;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.I2CManagerDevice.I2CManagerDeviceBuilder;
import org.arig.robot.exception.I2CException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * The Class AbstractI2CManagerTest.
 *
 * @author gdepuille
 */
@Slf4j
@ExtendWith(SpringExtension.class)
public class AbstractI2CManagerTest {

    private static AbstractI2CManager<Byte> impl;

    @BeforeAll
    public static void initClass() {
        impl = new DummyI2CManager();
    }

    @BeforeEach
    @SneakyThrows
    public void initTest() {
        impl.reset();

        final I2CManagerDevice<Byte> mux = I2CManagerDevice.<Byte>builder()
                .device((byte) 0)
                .deviceName("Board Multiplexeur")
                .build();
        impl.registerDevice(mux);
        impl.registerMultiplexerDevice(mux.deviceName(), new I2CMultiplexerDevice() {
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

    @Test
    public void testAlreadyRegisteredBoard() {
        Assertions.assertThrows(IllegalStateException.class,
                () -> impl.registerDevice(impl.getDeviceMap().values().iterator().next())
        );
    }

    @Test
    public void testRegisterBoardNull() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> impl.registerDevice(null)
        );
    }

    @Test
    public void testRegisterBoardNullDeviceName() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> {
                    I2CManagerDevice<Byte> d = I2CManagerDevice.<Byte>builder().device((byte) 1).build();
                    impl.registerDevice(d);
                }
        );
    }

    @Test
    public void testRegisterBoardEmptyDeviceName() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> {
                    I2CManagerDevice<Byte> d = I2CManagerDevice.<Byte>builder().device((byte) -1).deviceName("").build();
                    impl.registerDevice(d);
                }
        );
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

        Assertions.assertEquals(init + 3, impl.nbDeviceRegistered());
    }

    @Test
    public void testRegisterMultiplexedBoardWithoutChannel() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> impl.registerDevice(I2CManagerDevice.<Byte>builder()
                                .device((byte) 2)
                                .deviceName("Board multiplexe sans channel")
                                .multiplexerDeviceName("Board multiplexer")
                                .build())
        );
    }

    @Test
    @SneakyThrows
    public void testExecuteScanWithoutMapping() {
        impl.reset();
        Assertions.assertThrows(IllegalArgumentException.class, () -> impl.executeScan());
    }

    @Test
    public void textExecuteScan() throws I2CException {
        impl.executeScan();
        Assertions.assertTrue(impl.status());
    }

    @Test
    public void testGetUnknownBoard() throws I2CException {
        Assertions.assertThrows(I2CException.class, () -> impl.getDevice("Unknown Board"));
    }

    @Test
    public void testGetKnownBoard() throws I2CException {
        I2CManagerDevice<Byte> d = impl.getDevice("Board 2");
        Assertions.assertEquals(2, d.device(), 0);
    }
}
