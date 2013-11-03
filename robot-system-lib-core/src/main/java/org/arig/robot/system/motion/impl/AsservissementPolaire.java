package org.arig.robot.system.motion.impl;

import lombok.Setter;

import org.arig.robot.filters.IPidFilter;
import org.arig.robot.filters.impl.QuadRamp;
import org.arig.robot.system.encoders.Abstract2WheelsEncoders;
import org.arig.robot.system.motion.IAsservissement;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.arig.robot.vo.ConsignePolaire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * The Class AsservissementPolaire.
 * 
 * @author mythril
 */
public class AsservissementPolaire implements IAsservissement {

	/** The conv. */
	@Autowired
	private ConvertionRobotUnit conv;

	/** The consigne polaire. */
	@Autowired
	private ConsignePolaire consignePolaire;

	/** The encoders. */
	@Autowired
	private Abstract2WheelsEncoders encoders;

	/** The pid distance. */
	@Autowired
	@Qualifier("pidDistance")
	private IPidFilter pidDistance;

	/** The pid orientation. */
	@Autowired
	@Qualifier("pidOrientation")
	private IPidFilter pidOrientation;

	/** The filter distance. */
	@Autowired
	@Qualifier("rampDistance")
	private QuadRamp filterDistance;

	/** The filter orientation. */
	@Autowired
	@Qualifier("rampOrientation")
	private QuadRamp filterOrientation;

	/** The set point distance. */
	private double setPointDistance;

	/** The set point orientation. */
	private double setPointOrientation;

	/** The output distance. */
	private double outputDistance;

	/** The output orientation. */
	private double outputOrientation;

	/** The min fenetre distance. */
	@Setter
	private double minFenetreDistance;

	/** The min fenetre orientation. */
	@Setter
	private double minFenetreOrientation;

	/**
	 * Instantiates a new asservissement polaire.
	 */
	public AsservissementPolaire() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.arig.robot.system.motion.IAsservissement#reset()
	 */
	@Override
	public void reset() {
		reset(false);
	}

	/* (non-Javadoc)
	 * @see org.arig.robot.system.motion.IAsservissement#reset(boolean)
	 */
	@Override
	public void reset(final boolean resetFilters) {

	}

	/* (non-Javadoc)
	 * @see org.arig.robot.system.motion.IAsservissement#process()
	 */
	@Override
	public void process() {
		// Application du filtre pour la génération du profil trapézoidale et définition des consignes
		setPointDistance = filterDistance.filter(consignePolaire.getVitesseDistance(), consignePolaire.getConsigneDistance(), consignePolaire.isFrein());
		setPointOrientation = filterOrientation.filter(consignePolaire.getVitesseOrientation(), consignePolaire.getConsigneOrientation(), true); // Toujours le frein pour l'orientation

		// Calcul des filtre PID
		outputDistance = pidDistance.compute(setPointDistance, encoders.getDistance());
		outputOrientation = pidOrientation.compute(setPointOrientation, encoders.getOrientation());

		// Consigne moteurs
		consignePolaire.setCmdDroit((int) (outputDistance + outputOrientation));
		consignePolaire.setCmdGauche((int) (outputDistance - outputOrientation));
	}

	/**
	 * Méthode permettant de récuperer la zone pour la fenetre en distance.
	 *
	 * @return the fenetre approche distance
	 */
	public double getFenetreApprocheDistance() {
		// Application du théorème de Shannon
		// En gros l'idée est que la fenêtre varie en fonction de la vitesse afin qu'a pleine bourre on la dépasse pas
		// et que l'on se mette a faire des tours sur soit même
		return Math.max(minFenetreDistance, 3 * setPointDistance);
	}

	/**
	 * Méthode permettant de récuperer la zone pour la fenetre en distance.
	 *
	 * @return the fenetre approche orientation
	 */
	public double getFenetreApprocheOrientation() {
		// Application du théorème de Shannon
		// En gros l'idée est que la fenêtre varie en fonction de la vitesse afin qu'a pleine bourre on la dépasse pas
		// et que l'on se mette a faire des tours sur soit même
		return Math.max(minFenetreOrientation, 3 * setPointOrientation);
	}
}
