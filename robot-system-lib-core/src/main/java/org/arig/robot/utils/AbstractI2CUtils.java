package org.arig.robot.utils;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.arig.robot.utils.exception.I2CException;

/**
 * The Class AbstractI2CUtils.
 * 
 * @author mythril
 */
@Slf4j
public abstract class AbstractI2CUtils {

	/** The board map. */
	@Getter
	private final Map<String, Byte> boardMap = new HashMap<>();

	/**
	 * Scan.
	 *
	 * @throws RuntimeException the runtime exception
	 */
	public abstract void scan() throws RuntimeException;

	/**
	 * Checks if is error.
	 *
	 * @param returnCode the return code
	 * @return true, if is error
	 */
	public abstract boolean isError(final Byte returnCode);

	/**
	 * Checks if is ok.
	 *
	 * @param returnCode the return code
	 * @return true, if is ok
	 */
	public boolean isOk(final Byte returnCode) {
		return !isError(returnCode);
	}

	/**
	 * Register board.
	 *
	 * @param boardName the board name
	 * @param address the address
	 */
	public void registerBoard(final String boardName, final Byte address) {
		AbstractI2CUtils.log.info(String.format("Enregistrement de la carte %s a l'adresse %s."));
		boardMap.put(boardName, address);
	}

	/**
	 * Gets the board address.
	 *
	 * @param boardName the board name
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
