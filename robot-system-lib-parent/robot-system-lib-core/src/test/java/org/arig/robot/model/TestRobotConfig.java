package org.arig.robot.model;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Slf4j
@ExtendWith(SpringExtension.class)
public class TestRobotConfig {

  private static RobotConfig rc = new RobotConfig();

  @BeforeAll
  public static void initClass() {
    rc.vitesseMin(100);
    rc.vitesseOrientationMin(100);
    rc.vitesseMax(1000);
    rc.vitesseOrientationMax(1000);
    rc.vitesseDefRatio(10);
  }

  @Test
  public void testVitesse() {
    log.info("Test des valeurs ratio vitesse");

    for (int i = 0; i <= 100; i++) {
      log.info("PCT : {} : {} distance, {} orientation", i, rc.vitesse(i), rc.vitesseOrientation(i));
      Assertions.assertEquals(rc.vitesse(i), rc.vitesseOrientation(i));
    }
  }
}
