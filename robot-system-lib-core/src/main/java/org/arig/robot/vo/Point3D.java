package org.arig.robot.vo;

import lombok.*;

/**
 * Created by mythril on 04/01/14.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Point3D extends Point {

    /** The z. */
    private double z;

    public Point3D(double x, double y, double z) {
        super(x, y);
        this.z = z;

    }

    /**
     * Adds the delta z.
     *
     * @param dZ
     *            the d z
     */
    public void addDeltaZ(final double dZ) {
        z += dZ;
    }
}
