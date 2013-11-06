package org.arig.robot.utils;

import java.util.HashMap;
import java.util.Map;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.arig.robot.exception.I2CException;
import org.springframework.util.Assert;

/**
 * The Class AbstractI2CUtils.
 * 
 * @author mythril
 */
@Slf4j
public abstract class AbstractI2CUtils {

    /** The board map. */
    @Getter(value = AccessLevel.PROTECTED)
    private final Map<String, Byte> boardMap = new HashMap<>();

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
     * Reset.
     * 
     * @throws I2CException
     *             the i2 c exception
     */
    public abstract void reset() throws I2CException;

    /**
     * Scan.
     * 
     * @throws I2CException
     *             the i2 c exception
     */
    protected abstract void scan() throws I2CException;

    /**
     * Checks if is error.
     * 
     * @param returnCode
     *            the return code
     * @return true, if is error
     */
    public abstract boolean isError(final Byte returnCode);

    /**
     * Prints the error.
     * 
     * @param returnCode
     *            the return code
     */
    public abstract void printError(final Byte returnCode);

    /**
     * Checks if is ok.
     * 
     * @param returnCode
     *            the return code
     * @return true, if is ok
     */
    public boolean isOk(final Byte returnCode) {
        return !isError(returnCode);
    }

    /**
     * Register board.
     * 
     * @param boardName
     *            the board name
     * @param address
     *            the address
     */
    public void registerBoard(final String boardName, final Byte address) {
        Assert.notNull(address, "L'addresse doit être précisé");
        Assert.hasText(boardName, "Le nom de la carte doit être précisé");

        AbstractI2CUtils.log.debug(String.format("Enregistrement de la carte %s a l'adresse 0x%02X.", boardName, address));
        boardMap.put(boardName, address);
    }

    /**
     * Gets the board address.
     * 
     * @param boardName
     *            the board name
     * @return the board address
     * @throws I2CException
     */
    public Byte getBoardAddress(final String boardName) throws I2CException {
        if (boardMap.containsKey(boardName)) {
            return boardMap.get(boardName);
        }

        // FallBack
        final String message = String.format("Carte inconnu : %s", boardName);
        AbstractI2CUtils.log.warn(message);
        throw new I2CException(message);
    }
}
