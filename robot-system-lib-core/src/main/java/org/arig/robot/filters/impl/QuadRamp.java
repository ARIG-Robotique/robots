package org.arig.robot.filters.impl;

import lombok.extern.slf4j.Slf4j;

import org.arig.robot.utils.ConvertionRobotUnit;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The Class QuadRamp.
 * 
 * @author mythril
 */
@Slf4j
public class QuadRamp {

	/** The conv. */
	@Autowired
	private ConvertionRobotUnit conv;

	/** The sample time. */
	private double sampleTimeS;

	/** The ramp acc. */
	private double rampAcc;

	/** The ramp dec. */
	private double rampDec;

	/** The step vitesse accel. */
	private double stepVitesseAccel;

	/** The step vitesse decel. */
	private double stepVitesseDecel;

	/** The vitesse courante. */
	private double vitesseCourante;

	/** The distance decel. */
	private double distanceDecel;

	/** The ecart precedent. */
	private double ecartPrecedent;

	/**
	 * Instantiates a new quad ramp.
	 */
	public QuadRamp() {
		sampleTimeS = 0.010;
		rampAcc = 100.0;
		rampDec = 100.0;

		QuadRamp.log.info(String.format("Initialisation par défaut (SampleTime : %s ; Rampe ACC : %s ; Rampe DEC : %s", sampleTimeS, rampAcc, rampDec));

		reset();
		updateStepVitesse();
	}

	/**
	 * Instantiates a new quad ramp.
	 *
	 * @param sampleTimeMs the sample time in ms
	 * @param rampAcc the ramp acc
	 * @param rampDec the ramp dec
	 */
	public QuadRamp(final double sampleTimeS, final double rampAcc, final double rampDec) {
		this.sampleTimeS = sampleTimeS / 1000;
		this.rampAcc = rampAcc;
		this.rampDec = rampDec;

		QuadRamp.log.info(String.format("Initialisation (SampleTime : %s ; Rampe ACC : %s ; Rampe DEC : %s", sampleTimeS, rampAcc, rampDec));

		reset();
		updateStepVitesse();
	}

	/**
	 * Sets the sample time ms.
	 *
	 * @param value the new sample time ms
	 */
	public void setSampleTimeMs(final double value) {
		sampleTimeS = value / 1000;
		updateStepVitesse();
	}

	/**
	 * Sets the ramp acc.
	 *
	 * @param value the new ramp acc
	 */
	public void setRampAcc(final double value) {
		rampAcc = value;
		updateStepVitesse();
	}

	/**
	 * Sets the ramp dec.
	 *
	 * @param value the new ramp dec
	 */
	public void setRampDec(final double value) {
		rampDec = value;
		updateStepVitesse();
	}

	/**
	 * Update step vitesse.
	 */
	private void updateStepVitesse() {
		stepVitesseAccel = rampAcc * sampleTimeS;
		stepVitesseDecel = rampDec * sampleTimeS;
	}

	/**
	 * Reset.
	 */
	public void reset() {
		QuadRamp.log.info("Reset des paramètres");

		distanceDecel = 0;
		ecartPrecedent = 0;
		vitesseCourante = 0;
	}

	/**
	 * Application du filtre.
	 * Cette méthode est appelé depuis la sub routine d'asservissement
	 *
	 * @param vitesse the vitesse
	 * @param consigne the consigne
	 * @param frein the frein
	 * @return the double
	 */
	public double filter(final double vitesse, final double consigne, final boolean frein) {
		// Calcul de la distance de décéleration en fonction des parametres
		distanceDecel = conv.mmToPulse(vitesseCourante * vitesseCourante / (2 * rampDec));
		if (vitesseCourante > vitesse || Math.abs(consigne) <= distanceDecel && frein) {
			vitesseCourante -= stepVitesseDecel;
		} else if (vitesseCourante < vitesse) {
			vitesseCourante += stepVitesseAccel;
		}

		// Valeur max (evite les oscilations en régime établie)
		vitesseCourante = Math.min(vitesseCourante, vitesse);

		// Controle pour interdire les valeurs négatives
		vitesseCourante = Math.max(vitesseCourante, 0);

		// Calcul de la valeur théorique en fonction de la vitesse.
		final double pulseForVitesse = conv.mmToPulse(vitesseCourante) * sampleTimeS;

		// Consigne théorique en fonction de la vitesse
		double ecartTheorique = pulseForVitesse;
		if (consigne < 0) {
			ecartTheorique = -ecartTheorique;
		}

		return ecartTheorique;
	}

	/**
	 * /!\ EXPERIMENTAL
	 *
	 * Application du filtre "logarithmique".
	 * Cette méthode est appelé depuis la sub routine d'asservissement
	 *
	 * FIXME : ça merde lors de la phase de décéleration.
	 *
	 * @param vitesse the vitesse
	 * @param consigne the consigne
	 * @param mesure the mesure
	 * @param frein the frein
	 * @return the double
	 */
	public double filterLog(final double vitesse, final double consigne, final double mesure, final boolean frein) {
		// Récupération de la version normal et ajout de l'écart précedent
		final double ecartTheorique = filter(vitesse, consigne, frein) + ecartPrecedent;
		ecartPrecedent = ecartTheorique - mesure;

		/* TODO : Logger pour le CSV
		#ifdef DEBUG_MODE
			//Serial.print(";FOutLog");Serial.print(ecartTheorique);
		#endif
		 */
		return ecartTheorique;
	}
}
