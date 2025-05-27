package org.arig.robot.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AngleRange {

  private final double minDeg;
  private final double maxDeg;

  public boolean contains(double angleDeg) {
    angleDeg = normalize(angleDeg);
    if (minDeg <= maxDeg) {
      return angleDeg >= minDeg && angleDeg <= maxDeg;
    } else {
      // Plage qui passe par ±180° (ex: [150°, -170°])
      return angleDeg >= minDeg || angleDeg <= maxDeg;
    }
  }

  private double normalize(double angleDeg) {
    // Normalise dans [-180, 180]
    angleDeg = angleDeg % 360;
    if (angleDeg > 180) angleDeg -= 360;
    if (angleDeg < -180) angleDeg += 360;
    return angleDeg;
  }
}
