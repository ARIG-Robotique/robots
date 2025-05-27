package org.arig.robot.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AngleRangeTest {

  @Test
  void testPositiveAngleRange() {
    AngleRange angleRange = AngleRange.builder().minDeg(30).maxDeg(150).build();

    Assertions.assertFalse(angleRange.contains(20));
    Assertions.assertTrue(angleRange.contains(30));
    Assertions.assertTrue(angleRange.contains(35));
    Assertions.assertTrue(angleRange.contains(100));
    Assertions.assertTrue(angleRange.contains(150));
    Assertions.assertFalse(angleRange.contains(180));

    Assertions.assertFalse(angleRange.contains(-20));
    Assertions.assertFalse(angleRange.contains(-30));
    Assertions.assertFalse(angleRange.contains(-35));
    Assertions.assertFalse(angleRange.contains(-100));
    Assertions.assertFalse(angleRange.contains(-150));
    Assertions.assertFalse(angleRange.contains(-180));
  }

  @Test
  void testNegativeAngleRange() {
    AngleRange angleRange = AngleRange.builder().minDeg(-150).maxDeg(-30).build();

    Assertions.assertFalse(angleRange.contains(20));
    Assertions.assertFalse(angleRange.contains(30));
    Assertions.assertFalse(angleRange.contains(35));
    Assertions.assertFalse(angleRange.contains(100));
    Assertions.assertFalse(angleRange.contains(150));
    Assertions.assertFalse(angleRange.contains(180));

    Assertions.assertFalse(angleRange.contains(-20));
    Assertions.assertTrue(angleRange.contains(-30));
    Assertions.assertTrue(angleRange.contains(-35));
    Assertions.assertTrue(angleRange.contains(-100));
    Assertions.assertTrue(angleRange.contains(-150));
    Assertions.assertFalse(angleRange.contains(-180));
  }

  @Test
  void testCircularAngleRange() {
    AngleRange angleRange = AngleRange.builder().minDeg(80).maxDeg(-80).build();

    Assertions.assertFalse(angleRange.contains(20));
    Assertions.assertFalse(angleRange.contains(30));
    Assertions.assertFalse(angleRange.contains(35));
    Assertions.assertTrue(angleRange.contains(100));
    Assertions.assertTrue(angleRange.contains(150));
    Assertions.assertTrue(angleRange.contains(180));

    Assertions.assertFalse(angleRange.contains(-20));
    Assertions.assertFalse(angleRange.contains(-30));
    Assertions.assertFalse(angleRange.contains(-35));
    Assertions.assertTrue(angleRange.contains(-100));
    Assertions.assertTrue(angleRange.contains(-150));
    Assertions.assertTrue(angleRange.contains(-180));
  }
}
