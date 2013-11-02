package org.arig.robot.system.encoders;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class AbstractEncoders.
 * 
 * @author mythril
 */
@Slf4j
public abstract class AbstractEncoders {

	/** The distance. */
	@Getter
	private double distance;

	/** The orientation. */
	@Getter
	private double orientation;

	/** The coef gauche. */
	private double coefGauche;

	/** The coef droit. */
	private double coefDroit;

	/**
	 * Instantiates a new abstract encoders.
	 */
	protected AbstractEncoders() {
		distance = orientation = 0;
		coefDroit = coefGauche = 1.0;
	}

	/**
	 * Lecture valeurs.
	 */
	public void lectureValeurs() {
		final double gauche = lectureGauche() * coefGauche;
		final double droit = lectureDroit() * coefDroit;
		setValeursCodeurs(gauche, droit);

		// TODO : Ajouter le loggeur CSV

		AbstractEncoders.log.info(String.format("Lecture des valeurs codeurs : Gauche = %s ; Droit = %s", gauche, droit));
	}

	/**
	 * Sets the coefs.
	 *
	 * @param coefGauche the coef gauche
	 * @param coefDroit the coef droit
	 */
	public void setCoefs(final double coefGauche, final double coefDroit) {
		this.coefGauche = coefGauche;
		this.coefDroit = coefDroit;
	}

	/**
	 * Reset.
	 */
	public abstract void reset();

	/**
	 * Lecture gauche.
	 *
	 * @return the double
	 */
	protected abstract double lectureGauche();

	/**
	 * Lecture droit.
	 *
	 * @return the double
	 */
	protected abstract double lectureDroit();

	/**
	 * Sets the valeurs codeurs.
	 *
	 * @param gauche the gauche
	 * @param droit the droit
	 */
	private void setValeursCodeurs(final double gauche, final double droit) {
		distance = (droit + gauche) / 2;
		orientation = droit - gauche;
	}
}
