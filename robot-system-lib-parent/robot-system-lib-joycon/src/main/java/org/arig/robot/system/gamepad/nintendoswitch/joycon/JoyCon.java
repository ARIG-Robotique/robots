package org.arig.robot.system.gamepad.nintendoswitch.joycon;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.system.gamepad.nintendoswitch.NintendoSwitchHID;
import purejavahidapi.HidDevice;
import purejavahidapi.HidDeviceInfo;
import purejavahidapi.PureJavaHidApi;

import java.util.Map;

@Slf4j
@Accessors(fluent = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class JoyCon {
    private static final byte DEVICE_REPORT_IDS = 1;

    private final short joyConId;
    private final String name;

    private HidDevice device;

    @Getter
    private boolean connected = false;

    public boolean close() {
        boolean isClosed;
        try {
            if (device != null) {
                device.close();
            }
            isClosed = true;
        } catch (IllegalStateException e) {
            log.warn("Erreur lors de la fermeture de la connexion avec le JoyCon {} - {}", joyConId, name);
            isClosed = false;
        }
        return isClosed;
    }

    public void open() {
        HidDeviceInfo hidDeviceInfo = null;

        for (HidDeviceInfo info : PureJavaHidApi.enumerateDevices()) {
            if ((info.getVendorId() == NintendoSwitchHID.VENDOR_ID) && (info.getProductId() == joyConId)) {
                log.info("Nintendo gamepad {} détecté", info.getProductString());
                hidDeviceInfo = info;
            }
        }

        if (hidDeviceInfo == null) {
            log.info("Aucun device correspondant trouvé pour {} - {}", joyConId, name);
            return;
        }

        try {
            device = PureJavaHidApi.openDevice(hidDeviceInfo);
            log.info("Connecté a {}", device.getHidDeviceInfo().getProductString());
            connected = true;
            Thread.sleep(100);

            device.setInputReportListener(new JoyConInputListener(this));
            device.setDeviceRemovalListener(d -> {
                log.info("Déconnecté de {}", d.getHidDeviceInfo().getProductString());
                device.close();
                connected = false;
            });
            setModeHID();
            getCalibration();
            setLed((byte) 16);
            doVibration();
            setModeNormal();

        } catch (Exception e) {
            connected = false;
        }
    }

    protected abstract JoyConEventListener eventListener();
    protected abstract void processData(byte[] data);
    protected abstract Map<JoyConButton, Boolean> inputs();
    protected abstract float horizontal();
    protected abstract float vertical();
    protected abstract byte battery();
    protected abstract void saveCalibration(int[] factoryCal);

    @SneakyThrows
    private void sendData(byte[] data, long sleep) {
        device.setOutputReport(DEVICE_REPORT_IDS, data, data.length);
        Thread.sleep(sleep);
    }

    private void setModeHID() {
        byte[] data = new byte[16];
        data[9] = 0x03;
        data[10] = 0x3F;
        sendData(data, 100);
    }

    private void setModeNormal() {
        byte[] data = new byte[16];
        data[9] = 0x03;
        data[10] = 0x30;
        sendData(data, 16);
    }

    private void setLed(byte value) {
        byte[] data = new byte[16];
        data[9] = 0x30;
        data[10] = value;
        sendData(data, 100);
    }

    private void doVibration() {
        // Activation de la vibration
        byte[] data = new byte[16];
        data[9] = 0x48;
        data[10] = 0x01;
        sendData(data, 16);

        // Cycle de vibration
        data = new byte[16];
        data[1] = (byte) 0xc2;
        data[2] = (byte) 0xc8;
        data[3] = 0x03;
        data[4] = 0x72;
        sendData(data, 90);

        data = new byte[16];
        data[1] = 0x00;
        data[2] = 0x01;
        data[3] = 0x40;
        data[4] = 0x40;
        sendData(data, 16);

        data = new byte[16];
        data[1] = (byte) 0xc3;
        data[2] = (byte) 0xc8;
        data[3] = 0x60;
        data[4] = 0x64;
        sendData(data, 30);

        // Désactivation de la vibration
        data = new byte[16];
        data[9] = 0x48;
        data[10] = 0x00;
        sendData(data, 16);
    }

    private void getCalibration() {
        byte[] data = new byte[16];
        data[9] = 0x10;
        data[10] = 0x3D;
        data[11] = 0x60;
        data[14] = 0x12;
        sendData(data, 100);
    }
}
