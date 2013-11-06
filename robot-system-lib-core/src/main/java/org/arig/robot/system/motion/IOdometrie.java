package org.arig.robot.system.motion;

import org.arig.robot.vo.RobotPosition;
import org.arig.robot.vo.enums.TypeOdometrie;

/**
 * The Interface IOdometrie.
 * 
 * @author mythril
 */
public interface IOdometrie {

    /**
     * Gets the position.
     * 
     * @return the position
     */
    RobotPosition getPosition();

    /**
     * Gets the type.
     * 
     * @return the type
     */
    TypeOdometrie getType();

    /**
     * Inits the odometrie.
     * 
     * @param x
     *            the x
     * @param y
     *            the y
     * @param angle
     *            the angle
     */
    void initOdometrie(final double x, final double y, final int angle);

    /**
     * Calcul position.
     */
    void calculPosition();
}
