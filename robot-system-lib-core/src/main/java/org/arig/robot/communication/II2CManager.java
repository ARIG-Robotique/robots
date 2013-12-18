package org.arig.robot.communication;

import org.arig.robot.exception.I2CException;

/**
 * Created by mythril on 18/12/13.
 */
public interface II2CManager {

    /**
     * Execute un scan I2C afin de detecter que tous les périphérique enregistré sont bien présent.
     *
     * @throws I2CException
     */
    void executeScan() throws I2CException;

    /**
     * Reset.
     *
     * @throws org.arig.robot.exception.I2CException
     *             the i2 c exception
     */
    void reset() throws I2CException;

    /**
     * Checks if is error.
     *
     * @param returnCode
     *            the return code
     * @return true, if is error
     */
    boolean isError(final Byte returnCode);

    /**
     * Checks if is ok
     *
     * @param returnCode
     * @return
     */
    boolean isOk(final Byte returnCode);

    /**
     * Prints the error.
     *
     * @param returnCode
     *            the return code
     */
    void printError(final Byte returnCode);

    /**
     * Send data.
     *
     * @param deviceName
     *            the address
     * @param datas
     *            the datas
     * @return the byte
     */
    byte sendData(final String deviceName, final byte... datas);

    /**
     * Send data.
     *
     * @param deviceName
     *            the address
     * @param nbResult
     *            the nb result
     * @param datas
     *            the datas
     * @return the byte
     */
    byte sendData(final String deviceName, final int nbResult, final byte... datas);

    /**
     * Gets the data.
     *
     * @param deviceName
     *            the address
     * @return the data
     */
    byte getData(final String deviceName);

    /**
     * Gets the datas.
     *
     * @param deviceName
     *            the address
     * @return the datas
     */
    byte[] getDatas(final String deviceName);
}
