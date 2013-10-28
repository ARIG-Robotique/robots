package org.arig.robot.filters.impl;

import lombok.extern.slf4j.Slf4j;

import org.arig.robot.filters.IPidFilter;

/**
 * The Class SimplePID.
 *
 * @author mythril
 */
@Slf4j
public class SimplePID implements IPidFilter {

	/** The kp. */
	private double kp;

	/** The ki. */
	private double ki;

	/** The kd. */
	private double kd;

	/** The error sum. */
	private double errorSum;

	/** The last error. */
	private double lastError;

	/**
	 * Instantiates a new arig pid.
	 */
	public SimplePID() {
		super();

		kp = 0.8;
		kd = 0.2;
		ki = 0.1;

		SimplePID.log.info(String.format("Initialisation des paramètres PID ( Kp = %s ; Ki = %s ; Kd = %s )", kp, ki, kd));

		reset();
	}

	/* (non-Javadoc)
	 * @see org.arig.robot.filters.IPidFilter#setTunings(double, double, double)
	 */
	@Override
	public void setTunings(final double kp, final double ki, final double kd) {
		SimplePID.log.info(String.format("Initialisation des paramètres PID ( Kp = %s ; Ki = %s ; Kd = %s )", kp, ki, kd));

		this.kp = kp;
		this.kd = kd;
		this.ki = ki;
	}

	/* (non-Javadoc)
	 * @see org.arig.robot.filters.IPidFilter#reset()
	 */
	@Override
	public void reset() {
		SimplePID.log.info("Reset des paramètres du PID");

		errorSum = 0;
		lastError = 0;
	}

	/* (non-Javadoc)
	 * @see org.arig.robot.filters.IPidFilter#compute(double, double)
	 */
	@Override
	public double compute(final double consigne, final double mesure) {
		final double error = consigne - mesure;
		final double deltaError = error - lastError;
		errorSum += error;
		lastError = error;
		final double result = kp * error + ki * errorSum + kd * deltaError;

		// TODO : Ajouter un logger spécifique pour les données CSV
		/*#ifdef
		final DEBUG_MODE
		Serial.print(";");Serial.print(consigne);
		Serial.print(";");Serial.print(mesure);
		Serial.print(";");Serial.print(errorSum);
		Serial.print(";");Serial.print(result);
		#endif*/

		return result;
	}
}
