package org.arig.robot.communication;

import org.arig.robot.utils.AbstractI2CUtils;

/**
 * The Interface II2CManager.
 * 
 * @author mythril
 */
public interface II2CManager {

	/**
	 * Send data.
	 *
	 * @param address the address
	 * @param datas the datas
	 * @return the byte
	 */
	byte sendData(final short address, final short ... datas);

	/**
	 * Gets the data.
	 *
	 * @return the data
	 */
	short getData();

	/**
	 * Gets the utils.
	 *
	 * @return the utils
	 */
	AbstractI2CUtils getUtils();
}
