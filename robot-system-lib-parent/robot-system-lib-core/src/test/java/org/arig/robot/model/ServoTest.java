package org.arig.robot.model;

import org.arig.robot.model.servos.Servo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ServoTest {

  @Test
  public void testAngular() {
    Servo servo = new Servo()
      .angular()
      .angle(-44, 2430)
      .angle(0, 1960)
      .angle(90, 1090)
      .angle(135, 580)
      .build();

    Assertions.assertEquals(1090, servo.angleToPosition(90));
  }

}
