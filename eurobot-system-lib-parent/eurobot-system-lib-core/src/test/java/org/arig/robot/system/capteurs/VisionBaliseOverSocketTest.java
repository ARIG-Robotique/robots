package org.arig.robot.system.capteurs;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.arig.robot.model.balise.StatutBalise;
import org.arig.robot.utils.SocketUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.util.Base64;

@Slf4j
@ExtendWith(SpringExtension.class)
class VisionBaliseOverSocketTest {

    private static VisionBaliseOverSocket visionBalise;

    @BeforeAll
    @SneakyThrows
    static void initTest() {
        String host = "localhost";
        int port = 9042;
        Assumptions.assumeTrue(SocketUtils.serverListening(host, port));

        visionBalise = new VisionBaliseOverSocket(host, port);
        visionBalise.openSocket();
        Assertions.assertTrue(visionBalise.isOpen());
    }

    @AfterAll
    static void stopTest() {
        if (visionBalise != null) {
            visionBalise.end();
        }
    }

    @Test
    @SneakyThrows
    void testDetection() {
        visionBalise.startDetection();

        short tries = 4;
        StatutBalise statut;
        do {
            Thread.sleep(3000);
            statut = visionBalise.getStatut();
            log.info("Statut : {}", statut);
            tries--;

        } while ((statut == null || !statut.detectionOk()) && tries > 0);
    }

    @Test
    @SneakyThrows
    void testGetPhoto() {
        String imgStr = visionBalise.getPhoto().getData();
        byte[] img = Base64.getDecoder().decode(imgStr);
        File dest = new File("img.jpg");
        FileUtils.writeByteArrayToFile(dest, img);
        log.info("Photo : {}", dest.getAbsolutePath());
    }
}
