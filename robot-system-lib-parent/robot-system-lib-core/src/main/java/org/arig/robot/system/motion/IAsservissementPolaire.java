package org.arig.robot.system.motion;

/**
 * @author gdepuille on 05/01/15.
 */
public interface IAsservissementPolaire extends IAsservissement {
    void setRampDistance(double accel, double decel);
    void setRampOrientation(double accel, double decel);
}
