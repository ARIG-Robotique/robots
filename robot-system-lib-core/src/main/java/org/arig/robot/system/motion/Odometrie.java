package org.arig.robot.system.motion;

import lombok.Getter;
import lombok.Setter;

import org.arig.robot.system.encoders.AbstractEncoders;
import org.arig.robot.utils.ConvertionUtils;
import org.arig.robot.vo.RobotPosition;
import org.arig.robot.vo.enums.TypeOdometrie;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The Class Odometrie.
 * 
 * @author mythril
 */
public class Odometrie {

	/** The position. */
	@Getter
	private final RobotPosition position;

	/** The type. */
	@Setter
	@Getter
	private TypeOdometrie type;

	/** The encoders. */
	@Autowired
	private AbstractEncoders encoders;

	/** The conv. */
	@Autowired
	private ConvertionUtils conv;

	/**
	 * Instantiates a new odometrie.
	 * By default is configured in LINEAR type.
	 */
	public Odometrie() {
		this(TypeOdometrie.LINEAIRE);
	}

	/**
	 * Instantiates a new odometrie.
	 */
	public Odometrie(final TypeOdometrie type) {
		position = new RobotPosition();
		this.type = type;
		initOdometrie(0, 0, 0);
	}

	/**
	 * Inits the odometrie.
	 *
	 * @param x the x
	 * @param y the y
	 * @param angle the angle
	 */
	public void initOdometrie(final double x, final double y, final int angle) {
		position.updatePosition(x, y, angle);
	}

	/**
	 * Calcul de la position en fonction de la valeurs des codeurs.
	 *
	 * /!\ Cette méthode doit être appelé après la lecture des valeurs codeurs toutes les x ms.
	 */
	public void calculPosition() {
		switch (type) {
		case LINEAIRE: approxLineaire();break;
		case CIRCULAIRE: approxCirculaire();break;
		}

		// TODO : Loggeur CSV
		/*
		#ifdef DEBUG_MODE
			Serial.print(";");Serial.print(Conv.pulseToMm(position.getX()));
			Serial.print(";");Serial.print(Conv.pulseToMm(position.getY()));
			Serial.print(";");Serial.print((double) Conv.pulseToDeg(position.getAngle()));
		#endif
		 */
	}

	/**
	 * Ce calcul est effectué avec le postulat que durant le labs de temps écoulé le robot a roulé droit (pas en courbe).
	 * On parle donc d'approximation linéaire.
	 */
	private void approxLineaire() {
		double newTheta = position.getAngle() + encoders.getOrientation();

		// Ajustement a 2 PI près
		if (newTheta > conv.getPi2Pulse()) {
			newTheta -= conv.getPi2Pulse();
		} else if (newTheta < -conv.getPi2Pulse()) {
			newTheta += conv.getPi2Pulse();
		}

		// Calcul du déplacement sur X et Y en fonction de l'angle et de la distance
		final double thetaRad = conv.pulseToRad(newTheta);
		final double dX = encoders.getDistance() * Math.cos(thetaRad);
		final double dY = encoders.getDistance() * Math.sin(thetaRad);

		// Sauvegarde nouvelle position (en pulse)
		position.setAngle(newTheta);
		position.addDeltaX(dX);
		position.addDeltaY(dY);
	}

	/**
	 * Calcul selon le postulat que le robot roule en courbe
	 */
	private void approxCirculaire() {
		throw new RuntimeException("NOT YET IMPLEMENTED");
	}
}
