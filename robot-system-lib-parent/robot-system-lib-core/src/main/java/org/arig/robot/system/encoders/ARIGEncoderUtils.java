package org.arig.robot.system.encoders;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.II2CManager;
import org.arig.robot.exception.I2CException;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ARIGEncoderUtils {

    /**
     * Lecture data depuis nos cartes codeur.
     * <p>
     * 1) On envoi la commande de lecture.
     * 2) On récupère un short (2 octets car int sur 2 octet avec un AVR 8 bits)
     *
     * @param deviceName the deviceName
     *
     * @return the int
     *
     * @throws I2CException
     * @see <a href="https://github.com/ARIG-Robotique/quadratic-reader">GitHub Quadratic Reader</a>
     */
    public static int lectureData(II2CManager i2cManager, final String deviceName) throws I2CException {
        try {
            i2cManager.sendData(deviceName);
        } catch (I2CException e) {
            String message = String.format("Impossible de lire la valeur codeur pour la carte %s", deviceName);
            log.error(message);
            throw new I2CException(message, e);
        }

        final byte[] datas = i2cManager.getDatas(deviceName, 4);
        int value =  (datas[0] << 24) | (datas[1] << 16) | (datas[2] << 8) | (datas[3] & 0xFF);
        return value;
    }
}
