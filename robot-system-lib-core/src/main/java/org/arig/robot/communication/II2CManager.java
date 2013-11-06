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
     * @param address
     *            the address
     * @param datas
     *            the datas
     * @return the byte
     */
    byte sendData(final int address, final byte... datas);

    /**
     * Send data.
     * 
     * @param address
     *            the address
     * @param nbResult
     *            the nb result
     * @param datas
     *            the datas
     * @return the byte
     */
    byte sendData(final int address, final int nbResult, final byte... datas);

    /**
     * Gets the data.
     * 
     * @param address
     *            the address
     * @return the data
     */
    byte getData(final int address);

    /**
     * Gets the datas.
     * 
     * @param address
     *            the address
     * @return the datas
     */
    byte[] getDatas(final int address);

    /**
     * Gets the utils.
     * 
     * @return the utils
     */
    AbstractI2CUtils getUtils();
}
