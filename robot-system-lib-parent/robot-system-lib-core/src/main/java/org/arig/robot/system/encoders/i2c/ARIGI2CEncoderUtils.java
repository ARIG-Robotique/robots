package org.arig.robot.system.encoders.i2c;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.i2c.I2CManager;
import org.arig.robot.exception.I2CException;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ARIGI2CEncoderUtils {

  /**
   * Lecture data depuis nos cartes codeur.
   * <p>
   * 1) On envoi la commande de lecture.
   * 2) On récupère un short (2 octets car int sur 2 octet avec un AVR 8 bits)
   *
   * @param deviceName the deviceName
   * @return the int
   * @throws I2CException
   * @see <a href="https://github.com/ARIG-Robotique/quadratic-reader">GitHub Quadratic Reader</a>
   */
  public static int lectureData(I2CManager i2cManager, final String deviceName) throws I2CException {
    try {
      i2cManager.sendData(deviceName);
    } catch (I2CException e) {
      String message = String.format("Impossible de lire la valeur codeur pour la carte %s", deviceName);
      log.error(message);
      throw new I2CException(message, e);
    }

    final byte[] data = i2cManager.getData(deviceName, 2);
    short value = ((short) ((data[0] << 8) + (data[1] & 0xFF)));
    return value;
  }
}
