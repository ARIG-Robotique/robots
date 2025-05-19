package org.arig.robot.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author gdepuille on 04/01/14.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Point3D extends Point {

  private double z;

  public Point3D(double x, double y, double z) {
    super(x, y);
    this.z = z;

  }

  public void addDeltaZ(final double dZ) {
    z += dZ;
  }
}
