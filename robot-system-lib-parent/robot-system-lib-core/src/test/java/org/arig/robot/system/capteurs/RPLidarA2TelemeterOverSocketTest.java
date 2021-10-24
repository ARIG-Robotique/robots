package org.arig.robot.system.capteurs;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.arig.robot.model.lidar.DeviceInfos;
import org.arig.robot.model.lidar.HealthInfos;
import org.arig.robot.model.lidar.ScanInfos;
import org.arig.robot.model.lidar.enums.HealthState;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;

/**
 * @author gdepuille on 03/03/17.
 */
@Slf4j
@ExtendWith(SpringExtension.class)
public class RPLidarA2TelemeterOverSocketTest {

    private static RPLidarA2TelemeterOverSocket rpLidar;

    @BeforeAll
    @SneakyThrows
    public static void initTest() {
        final File socketFile = new File("/tmp/lidar.sock");
        Assumptions.assumingThat(socketFile.exists(), () -> {
            rpLidar = new RPLidarA2TelemeterOverSocket(socketFile);
            rpLidar.printDeviceInfo();
        });
    }

    @AfterAll
    public static void stopTest() {
        if (rpLidar != null) {
            rpLidar.end();
        }
    }

    @Test
    public void testDeviceInfos() {
        DeviceInfos infos = rpLidar.deviceInfo();
        Assertions.assertNotNull(infos);
        Assertions.assertEquals("1.20", infos.getFirmwareVersion());
        Assertions.assertEquals(2, (long) infos.getHardwareVersion());
        Assertions.assertEquals("CCD2FFC1E8839EF2C0E69EF714655405", infos.getSerialNumber());
    }

    @Test
    public void testHealthInfo() {
        HealthInfos infos = rpLidar.healthInfo();
        Assertions.assertNotNull(infos);
        Assertions.assertEquals(HealthState.OK, infos.getState());
    }

    @Test
    public void testGrabData() {
        HealthInfos health = rpLidar.healthInfo();
        if (health.getState() != HealthState.OK) {
            Assertions.fail("Erreur de santé du RPLidar " + health.getState());
        }

        rpLidar.startScan();

        int nb = 1;
        do {
            log.info("Récupération scan {} / 100", nb);

            ScanInfos scans = rpLidar.grabData();
            Assertions.assertNotNull(scans);
            Assertions.assertNotNull(scans.getIgnored());
            Assertions.assertNotNull(scans.getScan());
            Assertions.assertTrue(CollectionUtils.isNotEmpty(scans.getScan()));

            nb++;
        } while (nb <= 100);

        rpLidar.stopScan();
    }
}
