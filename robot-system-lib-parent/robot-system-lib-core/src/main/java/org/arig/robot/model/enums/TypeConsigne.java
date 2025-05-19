package org.arig.robot.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * The Enum TypeConsigne.
 *
 * @author gdepuille
 */
@AllArgsConstructor
public enum TypeConsigne {
  XY(1),
  DIST(2),
  ANGLE(4),
  LINE(8),
  CIRCLE(16);

  @Getter
  private final int value;
}
