package org.arig.robot.system.servos;

import lombok.extern.slf4j.Slf4j;

import org.arig.robot.communication.II2CManager;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The Class SD21Servos.
 * 
 * @author GregoryDepuille
 */
@Slf4j
public class SD21Servos {

	/** The Constant SD21_VERSION_REGISTER. */
	public static final byte SD21_VERSION_REGISTER = 0x40;

	/** The address. */
	protected byte address;

	/** The ret code. */
	protected byte retCode;

	/** The i2c manager. */
	@Autowired
	private II2CManager i2cManager;

	/**
	 * Instantiates a new s d21 servos.
	 *
	 * @param address the address
	 */
	public SD21Servos(final byte address) {
		this.address = address;
	}

	/**
	 * Gets the base register.
	 * Renvoi le registre de base pour un servo.
	 * Par éxemple pour le servo 1 :
	 *  0 : SPEED REGISTER
	 *  1 : LOW BYTE POSITION REGISTER
	 *  2 : HIGH BYTE POSITION REGISTER
	 *
	 * @param servoNb the servo nb
	 * @return the base register
	 */
	public static byte getBaseRegister(final byte servoNb) {
		return (byte) (servoNb * 3 - 3);
	}

	/**
	 * Sets the position.
	 *
	 * @param servoNb the servo nb
	 * @param position the position
	 */
	public void setPosition(final byte servoNb, final int position) {
		if (!checkServo(servoNb)) {
			return;
		}

		SD21Servos.log.info(String.format("Définition de la position du servo %d (Position = %d)", servoNb, position));
		final byte retCode = i2cManager.sendData(address, (byte) (SD21Servos.getBaseRegister(servoNb) + 1), (byte) (position & 0xFF), (byte) (position >> 8));
		if (i2cManager.getUtils().isError(retCode)) {
			i2cManager.getUtils().printError(retCode);
		}
	}

	/**
	 * Sets the speed.
	 *
	 * @param servoNb the servo nb
	 * @param speed the speed
	 */
	public void setSpeed(final byte servoNb, final byte speed) {
		if (!checkServo(servoNb)) {
			return;
		}

		SD21Servos.log.info(String.format("Définiion de la vitesse du servo %d (Vitesse = %d)", servoNb, speed));
		final byte retCode = i2cManager.sendData(address, SD21Servos.getBaseRegister(servoNb), speed);
		if (i2cManager.getUtils().isError(retCode)) {
			i2cManager.getUtils().printError(retCode);
		}
	}

	/**
	 * Sets the position and speed.
	 *
	 * @param servoNb the servo nb
	 * @param speed the speed
	 * @param position the position
	 */
	public void setPositionAndSpeed(final byte servoNb, final byte speed, final int position) {
		if (!checkServo(servoNb)) {
			return;
		}

		SD21Servos.log.info(String.format("Comande du servo %d (Vitesse = %d,  Position = %d)", servoNb, speed, position));
		final byte retCode = i2cManager.sendData(address, SD21Servos.getBaseRegister(servoNb), speed, (byte) (position & 0xFF), (byte) (position >> 8));
		if (i2cManager.getUtils().isError(retCode)) {
			i2cManager.getUtils().printError(retCode);
		}
	}

	/**
	 * Prints the version.
	 */
	public void printVersion() {
		final byte retCode = i2cManager.sendData(address, SD21Servos.SD21_VERSION_REGISTER);
		if (i2cManager.getUtils().isOk(retCode)) {
			final short version = i2cManager.getData();
			SD21Servos.log.info(String.format("SD21 ServoMotors (V : %s)", version));
		} else {
			i2cManager.getUtils().printError(retCode);
		}
	}

	/**
	 * Check servo.
	 *
	 * @param servoNb the servo nb
	 * @return true, if servo number are between 1 and 21. False otherwise
	 */
	private boolean checkServo(final byte servoNb) {
		final boolean result = servoNb >= 1 && servoNb <= 21;
		if (!result) {
			SD21Servos.log.warn(String.format("Numéro de servo moteur invalide : %d", servoNb));
		}
		return result;
	}
}
