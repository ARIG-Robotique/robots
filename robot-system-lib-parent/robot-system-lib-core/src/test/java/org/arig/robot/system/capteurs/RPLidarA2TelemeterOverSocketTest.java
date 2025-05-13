package org.arig.robot.system.capteurs;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.arig.robot.model.lidar.DeviceInfos;
import org.arig.robot.model.lidar.HealthInfos;
import org.arig.robot.model.lidar.ScanInfos;
import org.arig.robot.model.lidar.enums.HealthState;
import org.arig.robot.system.capteurs.socket.RPLidarA2TelemeterOverSocket;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * @author gdepuille on 03/03/17.
 */
@Slf4j
@ExtendWith(SpringExtension.class)
abstract class RPLidarA2TelemeterOverSocketTest {

    static RPLidarA2TelemeterOverSocket rpLidar;

    @AfterAll
    static void stopTest() {
        if (rpLidar != null) {
            rpLidar.end();
        }
    }

    @Test
    void testDeviceInfos() {
        DeviceInfos infos = rpLidar.deviceInfo();
        Assertions.assertNotNull(infos);
        if (StringUtils.isNotBlank(infos.getDriver())) {
            Assertions.assertEquals("rplidar", infos.getDriver());
        }

        boolean firstLidar = false;
        boolean secondLidar = false;
        try {
            // Lidar A2 premier achat
            Assertions.assertEquals("1.20", infos.getFirmwareVersion());
            Assertions.assertEquals(2, (long) infos.getHardwareVersion());
            Assertions.assertEquals("CCD2FFC1E8839EF2C0E69EF714655405", infos.getSerialNumber());
            firstLidar = true;
            log.info("Lidar A2 premier achat");
        } catch (AssertionError e) {
            log.info("Pas le Lidar A2 premier achat");
        }

        try {
            Assertions.assertEquals("1.25", infos.getFirmwareVersion());
            Assertions.assertEquals(5, (long) infos.getHardwareVersion());
            Assertions.assertEquals("DF889A87C5E392D3A5E49EF04F5D3D65", infos.getSerialNumber());
            secondLidar = true;
            log.info("Lidar A2 second achat");
        } catch (AssertionError e) {
            log.info("Pas le Lidar A2 second achat");
        }

        Assertions.assertTrue(firstLidar || secondLidar, "Ne correspond a aucun de nos lidars");
    }

    @Test
    void testHealthInfo() {
        HealthInfos infos = rpLidar.healthInfo();
        Assertions.assertNotNull(infos);
        Assertions.assertEquals(HealthState.OK, infos.getState());
    }

    @Test
    void testGrabData() {
        HealthInfos health = rpLidar.healthInfo();
        if (health.getState() != HealthState.OK) {
            Assertions.fail("Erreur de santé du RPLidar " + health.getState());
        }

        rpLidar.startScan();

        int nb = 1;
        int total = 5;
        do {
            log.info("Récupération scan {} / {}", nb, total);

            ScanInfos scans = rpLidar.grabData();
            Assertions.assertNotNull(scans);
            Assertions.assertNotNull(scans.getIgnored());
            Assertions.assertTrue(CollectionUtils.isNotEmpty(scans.getScan()));

            nb++;
        } while (nb <= total);

        rpLidar.stopScan();
    }
}
