package org.arig.robot.system.motion.impl;

import org.arig.robot.system.motion.AbstractOdometrie;
import org.arig.robot.vo.enums.TypeOdometrie;

/**
 * The Class Odometrie.
 * 
 * @author mythril
 */
public class OdometrieCirculaire extends AbstractOdometrie {

	/**
	 * Instantiates a new circular odometrie.
	 */
	public OdometrieCirculaire() {
		super(TypeOdometrie.CIRCULAIRE);
	}

	/**
	 * Calcul selon le postulat que le robot roule en courbe
	 */
	@Override
	protected void process() {
		throw new RuntimeException("NOT YET IMPLEMENTED");
	}
}
