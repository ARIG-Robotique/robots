package org.arig.robot.communication;

import org.arig.robot.exception.I2CException;

/**
 * @author gdepuille on 18/12/13.
 */
public interface I2CManager {

    /**
     * Etat du bus I2C
     *
     * @return
     */
    boolean status();

    /**
     * Execute un scan I2C afin de detecter que tous les périphérique enregistré sont bien présent.
     *
     * @throws I2CException
     */
    void executeScan() throws I2CException;

    /**
     * Reset.
     *
     * @throws org.arig.robot.exception.I2CException the i2 c exception
     */
    void reset() throws I2CException;

    /**
     * Enregistrement des multiplexeurs
     *
     * @param multiplexerDeviceName Nom du multiplexeur
     * @param multiplexerDevice Implémentation du multiplexeur
     */
    void registerMultiplexerDevice(String multiplexerDeviceName, I2CMultiplexerDevice multiplexerDevice);

    /**
     * Send data.
     *
     * @param deviceName the address
     * @param data       the data
     */
    void sendData(final String deviceName, final byte... data) throws I2CException;

    /**
     * Gets the data.
     *
     * @param deviceName the address
     *
     * @return the data
     */
    byte getData(final String deviceName) throws I2CException;

    /**
     * Gets the data.
     *
     * @param deviceName the address
     *
     * @return the data
     */
    byte[] getData(final String deviceName, final int size) throws I2CException;
}
