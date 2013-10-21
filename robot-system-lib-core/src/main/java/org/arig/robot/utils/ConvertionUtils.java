package org.arig.robot.utils;

import lombok.Getter;

/**
 * The Class ConvertionUtils.
 * 
 * Cette classe permet de réalisé les changements d'unité.
 * Elle réalie les convertions pulse <-> mm et pulse <-> °
 * 
 * @author mythril
 */
public final class ConvertionUtils {

	/** The count per mm. */
	@Getter
	private final double countPerMm;

	/** The count per deg. */
	@Getter
	private final double countPerDegree;

	/** The pi pulse. */
	@Getter
	private final double piPulse;

	/** The pi2 pulse. */
	@Getter
	private final double pi2Pulse;

	/**
	 * Instantiates a new convertion utils.
	 */
	public ConvertionUtils(final double countPerMm, final double countPerDegree) {
		this.countPerMm = countPerMm;
		this.countPerDegree = countPerDegree;

		piPulse = degToPulse(180);
		pi2Pulse = degToPulse(360);
	}

	/**
	 * Mm to pulse.
	 *
	 * @param val the val
	 * @return the double
	 */
	public double mmToPulse(final double val) {
		return val * countPerMm;
	}

	/**
	 * Pulse to mm.
	 *
	 * @param val the val
	 * @return the double
	 */
	public double pulseToMm(final double val) {
		return val / countPerMm;
	}

	/**
	 * Deg to pulse.
	 *
	 * @param val the val
	 * @return the double
	 */
	public double degToPulse(final double val) {
		return val * countPerDegree;
	}

	/**
	 * Pulse to deg.
	 *
	 * @param val the val
	 * @return the double
	 */
	public double pulseToDeg(final double val) {
		return val / countPerDegree;
	}

	/**
	 * Pulse to rad.
	 *
	 * @param val the val
	 * @return the double
	 */
	public double pulseToRad(final double val) {
		return Math.toRadians(pulseToDeg(val));
	}

	/**
	 * Rad to pulse.
	 *
	 * @param val the val
	 * @return the double
	 */
	public double radToPulse(final double val) {
		return Math.toDegrees(degToPulse(val));
	}
}
