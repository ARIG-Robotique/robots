package org.arig.robot.communication.can;

import org.arig.robot.exception.CANException;

/**
 * @author gdepuille on 18/12/13.
 */
public interface CANManager {

    /**
     * Etat du manager après scan.
     *
     * @return
     */
    boolean scanStatus();

    /**
     * Execute un scan CAN afin de detecter que tous les périphérique enregistré sont bien présent.
     *
     * @throws CANException
     */
    void executeScan() throws CANException;

    /**
     * Reset.
     *
     * @throws CANException the i2 c exception
     */
    void reset() throws CANException;

    /**
     * Send data.
     *
     * @param deviceName the address
     * @param data       the data
     */
    void sendData(final String deviceName, final byte... data) throws CANException;

    /**
     * Gets the data.
     *
     * @param deviceName the address
     *
     * @return the data
     */
    byte getData(final String deviceName) throws CANException;

    /**
     * Gets the data.
     *
     * @param deviceName the address
     *
     * @return the data
     */
    byte[] getData(final String deviceName, final int size) throws CANException;
}
