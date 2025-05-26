package org.arig.robot.system.capteurs;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.arig.robot.model.lidar.DeviceInfos;
import org.arig.robot.model.lidar.HealthInfos;
import org.arig.robot.model.lidar.ScanInfos;
import org.arig.robot.model.lidar.enums.HealthState;
import org.arig.robot.system.capteurs.socket.LD19LidarTelemeterOverSocket;
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
abstract class LD19LidarTelemeterOverSocketTest {

  static LD19LidarTelemeterOverSocket ldLidar;

  @AfterAll
  static void stopTest() {
    if (ldLidar != null) {
      ldLidar.end();
    }
  }

  @Test
  void testDeviceInfos() {
    DeviceInfos infos = ldLidar.deviceInfo();
    Assertions.assertNotNull(infos);

    Assertions.assertEquals("ldlidar", infos.getDriver());
    Assertions.assertEquals("v2.3.1", infos.getFirmwareVersion());
    Assertions.assertNull(infos.getHardwareVersion());
    Assertions.assertNull(infos.getSerialNumber());
  }

  @Test
  void testHealthInfo() {
    HealthInfos infos = ldLidar.healthInfo();
    Assertions.assertNotNull(infos);
    Assertions.assertEquals(HealthState.OK, infos.getState());
  }

  @Test
  void testGrabData() {
    HealthInfos health = ldLidar.healthInfo();
    if (health.getState() != HealthState.OK) {
      Assertions.fail("Erreur de santé du RPLidar " + health.getState());
    }

    ldLidar.startScan();

    int total = 1500;
    int nb = 1;
    int nbFailed = 0;
    do {
      log.info("Récupération scan {} / {}", nb, total);

      try {
        ScanInfos scans = ldLidar.grabData();
        //Assertions.assertNotNull(scans);
        //Assertions.assertNotNull(scans.getIgnored());
        //Assertions.assertTrue(CollectionUtils.isNotEmpty(scans.getScan()));
      } catch (Exception e) {
        log.error("Erreur lors de la récupération du scan {}", nb);
        nbFailed++;
      }
      nb++;

    } while (nb <= total);

    log.info("Nombre de scans total : {}", nb - 1);
    log.info("Nombre de scans échoués : {}", nbFailed);

    ldLidar.stopScan();
  }
}
