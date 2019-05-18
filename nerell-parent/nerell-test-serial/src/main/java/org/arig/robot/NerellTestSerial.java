package org.arig.robot;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.services.I2COverSerial;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

//@SpringBootApplication
@Slf4j
public class NerellTestSerial {

    public static void main(final String[] args) {
//        SpringApplication.run(NerellTestSerial.class, args);

        log.info("Startup");

        final I2COverSerial i2COverSerial = I2COverSerial.getInstance();


        try {
            while (true) {

                // lecteur codeurs arduino
                byte address = (byte) 0x30;

                i2COverSerial.send((byte) 0xBE, address).get();
                i2COverSerial.send((byte) 0xBF, address, (byte) 0x02).get();

                // SRF 02
//                byte address = (byte) 0xE2 / 2;
//
//                // demande de mesure
//                log.info("Demande de mesure");
//                i2COverSerial.send((byte) 0xBE, address, (byte) 0x00, (byte) 0x51).get();
//
//                ThreadUtils.sleep(70);
//
//                // positionnement du registre
//                log.info("Changement de registre");
//                i2COverSerial.send((byte) 0xBE, address, (byte) 0x02).get();
//
//                // lecture
//                log.info("Lecture");
//                byte[] data = i2COverSerial.send((byte) 0xBF, address, (byte) 0x02).get();
//                int res = ((short) ((data[0] << 8) + (data[1] & 0xFF)));
//                log.info("Distance : {}", res);
//
//                ThreadUtils.sleep(100);
            }

        } catch (IOException | InterruptedException | ExecutionException e) {
            log.warn(e.getMessage());
        }
    }


}
