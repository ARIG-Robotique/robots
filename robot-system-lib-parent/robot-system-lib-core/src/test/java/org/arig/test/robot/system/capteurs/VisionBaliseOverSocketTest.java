package org.arig.test.robot.system.capteurs;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ThreadUtils;
import org.arig.robot.model.lidar.DeviceInfos;
import org.arig.robot.model.lidar.HealthInfos;
import org.arig.robot.model.lidar.ScanInfos;
import org.arig.robot.model.lidar.enums.HealthState;
import org.arig.robot.system.capteurs.RPLidarA2TelemeterOverSocket;
import org.arig.robot.system.capteurs.VisionBaliseOverSocket;
import org.arig.robot.utils.ImageUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.Socket;
import java.nio.file.Files;

/**
 * @author gdepuille on 03/03/17.
 */
@Slf4j
@RunWith(BlockJUnit4ClassRunner.class)
public class VisionBaliseOverSocketTest {

    private static VisionBaliseOverSocket visionBalise;

    @BeforeClass
    @SneakyThrows
    public static void initTest() {
        Assume.assumeTrue("Contrôle par la présence de la balise vision", serverListening("localhost", 9042));
        visionBalise = new VisionBaliseOverSocket("localhost", 9042);
        visionBalise.openSocket();
        Assert.assertTrue(visionBalise.isOpen());
    }

    @AfterClass
    public static void stopTest() {
        if (visionBalise != null) {
            visionBalise.end();
        }
    }

    @Test
    @SneakyThrows
    public void testGlobal() {
        visionBalise.startEtallonage();
        int i = 0;
        while(!visionBalise.getStatut().isEtallonageOk() && i < 4) {
            i++;
            Thread.sleep(3000);
        }

        visionBalise.startDetection();
    }

    @Test
    @SneakyThrows
    public void testGetPhoto() {
        byte[] img = visionBalise.getPhoto(600);
        File dest = new File("img.jpg");
        FileUtils.writeByteArrayToFile(dest, img);
        log.info("Photo : {}", dest.getAbsolutePath());
    }


    public static boolean serverListening(String host, int port) {
        try (Socket s = new Socket(host, port)) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
