package org.arig.robot.vo.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * The Enum TypeConsigne.
 * 
 * @author mythril
 */
@AllArgsConstructor
public enum TypeConsigne {

	/** The xy. */
	XY(1),

	/** The dist. */
	DIST(2),

	/** The angle. */
	ANGLE(4),

	/** The line. */
	LINE(8),

	/** The circle. */
	CIRCLE(16);

	/** The value. */
	@Getter
	private final int value;
}
