package org.arig.robot.system.encoders;

import lombok.extern.slf4j.Slf4j;

import org.arig.robot.communication.II2CManager;
import org.arig.robot.exception.I2CException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The Class ARIG2WheelsEncoders.
 *
 * @author mythril
 */
@Slf4j
public class ARIG2WheelsEncoders extends Abstract2WheelsEncoders {

	/** The address droit. */
	private final byte addressDroit;

	/** The address gauche. */
	private final byte addressGauche;

	/** The i2c manager. */
	@Autowired
	private II2CManager i2cManager;

	/**
	 * Instantiates a new aRIG encoders.
	 *
	 * @param addressGauche the address gauche
	 * @param addressDroit the address droit
	 */
	public ARIG2WheelsEncoders(final byte addressGauche, final byte addressDroit) {
		this.addressGauche = addressGauche;
		this.addressDroit = addressDroit;
	}

	/* (non-Javadoc)
	 * @see org.arig.robot.system.encoders.AbstractEncoders#reset()
	 */
	@Override
	public void reset() {
		ARIG2WheelsEncoders.log.info("Reset carte codeur droit");
		lectureDroit();

		ARIG2WheelsEncoders.log.info("Reset carte codeur gauche");
		lectureGauche();
	}

	/* (non-Javadoc)
	 * @see org.arig.robot.system.encoders.AbstractEncoders#lectureGauche()
	 */
	@Override
	protected double lectureGauche() {
		try {
			return lectureData(addressGauche);
		} catch (final I2CException e) {
			ARIG2WheelsEncoders.log.error("Erreur lors de la lecture du codeur gauche : " + e.toString());
			return 0;
		}
	}

	/* (non-Javadoc)
	 * @see org.arig.robot.system.encoders.AbstractEncoders#lectureDroit()
	 */
	@Override
	protected double lectureDroit() {
		try {
			return lectureData(addressDroit);
		} catch (final I2CException e) {
			ARIG2WheelsEncoders.log.error("Erreur lors de la lecture du codeur droit : " + e.toString());
			return 0;
		}
	}

	/**
	 * Lecture data depuis nos cartes codeur Arduino.
	 * {@link https://www.gitorious.org/arig-association/quadratic-reader/}
	 *
	 * 1) On envoi la commande de lecture.
	 * 2) On récupère 2 octets (int sur 2 octet avec un AVR 8 bits)
	 *
	 * @param address the address
	 * @return the int
	 * @throws I2CException
	 */
	private int lectureData(final byte address) throws I2CException {
		final byte retCode = i2cManager.sendData(address, 2);
		if (i2cManager.getUtils().isError(retCode)) {
			i2cManager.getUtils().printError(retCode);
			throw new I2CException("Impossible de lire la valeur codeur pour la carte " + address);
		}

		int value = 0;
		final byte[] datas = i2cManager.getDatas(address);
		value = datas[0] << 8;
		value += datas[1];

		ARIG2WheelsEncoders.log.info(String.format("Lecture de la valeur %s pour le codeur %d", value, address));
		return value;
	}
}
