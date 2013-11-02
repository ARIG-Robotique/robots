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
	private double angle;

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
	public void updatePosition(final double x, final double y, final double angle) {
		setX(x);
		setY(y);
		setAngle(angle);
	}

	/**
	 * Adds the delta x.
	 *
	 * @param dX the d x
	 */
	public void addDeltaX(final double dX) {
		x += dX;
	}

	/**
	 * Adds the delta y.
	 *
	 * @param dY the d y
	 */
	public void addDeltaY(final double dY) {
		y += dY;
	}
}
