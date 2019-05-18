package org.arig.robot.services;

import com.pi4j.io.serial.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Service
@Slf4j
public class I2COverSerial {

    private static int NCD_HEADER = 0xAA;

    private static I2COverSerial INSTANCE;

    private Serial serial;

    private CompletableFuture<byte[]> future;

    public static I2COverSerial getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new I2COverSerial();
        }

        return INSTANCE;
    }

    private I2COverSerial() {
        try {
            serial = SerialFactory.createInstance();

            serial.addListener(new SerialDataEventListener() {
                @Override
                public void dataReceived(SerialDataEvent event) {
                    onData(event);
                }
            });

            SerialConfig config = new SerialConfig()
                    .device("/dev/ttyUSB0")
                    .baud(Baud._115200)
                    .dataBits(DataBits._8)
                    .parity(Parity.NONE)
                    .stopBits(StopBits._1)
                    .flowControl(FlowControl.NONE);

            serial.open(config);
            serial.setBufferingDataReceived(false);

        } catch (IOException e) {
            log.warn(e.getMessage(), e);
        }
    }

    /**
     * Reception de data
     */
    private synchronized void onData(SerialDataEvent event) {
        try {
            byte[] data = event.getBytes();

            log.debug("[HEX DATA] {}", byteArrayToStr(data));

            if (data[0] == NCD_HEADER) {
                throw new IOException("Invalid NCD header");
            }

            byte[] actualData = new byte[data[1]];
            System.arraycopy(data, 2, actualData, 0, actualData.length);

            byte expectedChecksum = ncdChecksum(actualData);
            byte checksum = data[data.length - 1];

            if (checksum != expectedChecksum) {
                throw new IOException("Invalid NCD checksum, actual " + checksum + ", expected " + expectedChecksum + "");
            }

            future.complete(actualData);

        } catch (IOException e) {
            log.warn(e.getMessage());
            future.completeExceptionally(e);
        }

        future = null;
    }

    /**
     * Envoi d'un payload I2C brut
     */
    public synchronized Future<byte[]> send(byte... data) throws IOException {
        if (future != null) {
            log.warn("Device is busy");
            throw new IOException();
        }

        future = new CompletableFuture<>();

        try {
            byte[] alldata = new byte[data.length + 3];
            alldata[0] = (byte) NCD_HEADER;
            alldata[1] = (byte) data.length;
            System.arraycopy(data, 0, alldata, 2, data.length);
            alldata[alldata.length - 1] = ncdChecksum(data);

            log.debug("Send {}", byteArrayToStr(alldata));

            serial.write(alldata);
            serial.flush();

        } catch (IOException e) {
            log.warn(e.getMessage());
            throw e;
        }

        return future;
    }

    private byte ncdChecksum(byte[] data) {
        int checksum = NCD_HEADER + data.length;
        for (byte datum : data) {
            checksum += datum;
        }
        return (byte) (checksum & 0xFF);
    }


    final private static char[] hexArray = "0123456789ABCDEF".toCharArray();

    private String byteArrayToStr(byte... data) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            int v = data[i] & 0xff;
            sb.append(hexArray[v >> 4]);
            sb.append(hexArray[v & 0xf]);
        }
        return sb.toString();
    }

}
