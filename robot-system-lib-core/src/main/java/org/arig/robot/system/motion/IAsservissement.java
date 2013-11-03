package org.arig.robot.system.motion;

/**
 * The Interface IAsservissement.
 * 
 * @author mythril
 */
public interface IAsservissement {

	/**
	 * Process.
	 */
	void process();

	/**
	 * Reset.
	 */
	void reset();

	/**
	 * Reset.
	 *
	 * @param resetFilters the reset filters
	 */
	void reset(final boolean resetFilters);
}
