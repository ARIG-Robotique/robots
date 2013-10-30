package org.arig.robot.vo;

import lombok.Data;

/**
 * The Class RobotPosition.
 * 
 * @author mythril
 */

/**
 * Instantiates a new robot position.
 */
@Data
public class RobotPosition {

	/** The x. */
	private double x;

	/** The y. */
	private double y;

	/** The angle. */
	private int angle;

	/**
	 * Instantiates a new robot position.
	 */
	public RobotPosition() {
		updatePosition(0, 0, 0);
	}

	/**
	 * Update position.
	 *
	 * @param x the x
	 * @param y the y
	 * @param angle the angle
	 */
	public void updatePosition(final double x, final double y, final int angle) {
		setX(x);
		setY(y);
		setAngle(angle);
	}
}
