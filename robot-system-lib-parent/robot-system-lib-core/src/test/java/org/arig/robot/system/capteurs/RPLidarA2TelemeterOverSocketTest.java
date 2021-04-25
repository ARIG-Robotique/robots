package org.arig.robot.system.capteurs;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.arig.robot.model.lidar.DeviceInfos;
import org.arig.robot.model.lidar.HealthInfos;
import org.arig.robot.model.lidar.ScanInfos;
import org.arig.robot.model.lidar.enums.HealthState;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.io.File;

/**
 * @author gdepuille on 03/03/17.
 */
@Slf4j
@RunWith(BlockJUnit4ClassRunner.class)
public class RPLidarA2TelemeterOverSocketTest {

    private static RPLidarA2TelemeterOverSocket rpLidar;

    @BeforeClass
    @SneakyThrows
    public static void initTest() {
        final File socketFile = new File("/tmp/lidar.sock");
        Assume.assumeTrue("Contrôle initialisation RPLidar par la présence de la socket", socketFile.exists());

        rpLidar = new RPLidarA2TelemeterOverSocket(socketFile);
        rpLidar.printDeviceInfo();
    }

    @AfterClass
    public static void stopTest() {
        if (rpLidar != null) {
            rpLidar.end();
        }
    }

    @Test
    public void testDeviceInfos() {
        DeviceInfos infos = rpLidar.deviceInfo();
        Assert.assertNotNull(infos);
        Assert.assertEquals("1.20", infos.getFirmwareVersion());
        Assert.assertEquals(2, (long) infos.getHardwareVersion());
        Assert.assertEquals("CCD2FFC1E8839EF2C0E69EF714655405", infos.getSerialNumber());
    }

    @Test
    public void testHealthInfo() {
        HealthInfos infos = rpLidar.healthInfo();
        Assert.assertNotNull(infos);
        Assert.assertEquals(HealthState.OK, infos.getState());
    }

    @Test
    public void testGrabData() {
        HealthInfos health = rpLidar.healthInfo();
        if (health.getState() != HealthState.OK) {
            Assert.fail("Erreur de santé du RPLidar " + health.getState());
        }

        rpLidar.startScan();

        int nb = 1;
        do {
            log.info("Récupération scan {} / 100", nb);

            ScanInfos scans = rpLidar.grabData();
            Assert.assertNotNull(scans);
            Assert.assertNotNull(scans.getIgnored());
            Assert.assertNotNull(scans.getScan());
            Assert.assertTrue(CollectionUtils.isNotEmpty(scans.getScan()));

            nb++;
        } while(nb <= 100);

        rpLidar.stopScan();
    }
}
