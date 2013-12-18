package org.arig.robot.communication;

import java.util.HashMap;
import java.util.Map;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.arig.robot.exception.I2CException;
import org.springframework.util.Assert;

/**
 * The Class AbstractI2CManager.
 * 
 * @author mythril
 */
@Slf4j
public abstract class AbstractI2CManager<D> implements II2CManager {

    /** The board map. */
    @Getter(value = AccessLevel.PROTECTED)
    private final Map<String, D> boardMap = new HashMap<>();

    /**
     * Nb board registered.
     * 
     * @return the int
     */
    public int nbBoardRegistered() {
        return boardMap.size();
    }

    /**
     * Execute scan.
     * 
     * @throws I2CException
     *             the i2 c exception
     */
    public final void executeScan() throws I2CException {
        Assert.notEmpty(boardMap, "Le mapping des cartes et address est obligatoire");

        scan();
    }

    /**
     * Scan.
     *
     * @throws I2CException
     *             the i2 c exception
     */
    protected abstract void scan() throws I2CException;

    /**
     * Register board.
     * 
     * @param deviceName
     *            the board name
     * @param device
     *            the address
     */
    public void registerDevice(final String deviceName, D device) {
        Assert.notNull(device, "Le device doit être précisé");
        Assert.hasText(deviceName, "Le nom de la carte doit être précisé");

        AbstractI2CManager.log.debug(String.format("Enregistrement de la carte %s (%s).", deviceName, device.toString()));
        boardMap.put(deviceName, device);
    }

    /**
     * Gets the board.
     * 
     * @param deviceName
     *            the board name
     * @return the device
     * @throws I2CException
     */
    public D getDevice(final String deviceName) throws I2CException {
        if (boardMap.containsKey(deviceName)) {
            return boardMap.get(deviceName);
        }

        // FallBack
        final String message = String.format("Carte inconnu : %s", deviceName);
        AbstractI2CManager.log.warn(message);
        throw new I2CException(message);
    }

    /**
     * Checks if is ok
     *
     * @param returnCode
     * @return
     */
    public boolean isOk(final Byte returnCode) {
        return !isError(returnCode);
    }

}
