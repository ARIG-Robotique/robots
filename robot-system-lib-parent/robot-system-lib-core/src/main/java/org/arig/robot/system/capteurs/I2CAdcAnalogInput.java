package org.arig.robot.system.capteurs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.II2CManager;
import org.arig.robot.exception.I2CException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Classe d'interface avec le composant I2C ADC de Gravitech
 *
 * @author gregorydepuille
 * @see <a href="http://www.gravitech.us/i2c128anco.html">I2C ADC documentation</a>
 */
@Slf4j
@Data
@AllArgsConstructor
public class I2CAdcAnalogInput {

    private static final byte[] channels = new byte[]{0, 4, 1, 5, 2, 6, 3, 7};

    public static final byte ADC_1 = (byte) 0;
    public static final byte ADC_2 = (byte) 1;
    public static final byte ADC_3 = (byte) 2;
    public static final byte ADC_4 = (byte) 3;
    public static final byte ADC_5 = (byte) 4;
    public static final byte ADC_6 = (byte) 5;
    public static final byte ADC_7 = (byte) 6;
    public static final byte ADC_8 = (byte) 7;

    @AllArgsConstructor
    public enum InputMode {
        DIFFERENTIAL_INPUTS((byte) 0),
        SINGLE_ENDED_INPUTS((byte) 1);

        @Getter
        private byte value;
    }

    @AllArgsConstructor
    public enum PowerMode {
        POWER_DOWN_BETWEEN_AD((byte) 0),
        INTERNAL_REF_OFF_AD_ON((byte) 1),
        INTERNAL_REF_ON_AD_OFF((byte) 2),
        INTERNAL_REF_ON_AD_ON((byte) 3);

        @Getter
        private byte value;
    }

    @Autowired
    private II2CManager i2cManager;

    private final String deviceName;

    private InputMode inputMode = InputMode.SINGLE_ENDED_INPUTS;

    private PowerMode powerMode = PowerMode.POWER_DOWN_BETWEEN_AD;

    public I2CAdcAnalogInput(String deviceName) {
        this.deviceName = deviceName;
    }

    protected byte getRegistre(byte capteurId) {
        return (byte) ((inputMode.getValue() << 7) + (channels[capteurId] << 4) + (powerMode.getValue() << 2));
    }

    public int readCapteurValue(byte capteurId) throws I2CException {
        if (log.isDebugEnabled()) {
            log.debug("Lecture analogique du capteur {}", capteurId);
        }
        try {
            i2cManager.sendData(deviceName, getRegistre(capteurId));
        } catch (I2CException e) {
            log.error("Impossible de lire la valeur du capteur analogique pour la carte " + deviceName);
            throw new I2CException("Impossible de lire la valeur codeur pour la carte " + deviceName, e);
        }

        final byte[] datas = i2cManager.getDatas(deviceName, 2);
        short res = ((short) ((datas[0] << 8) + (datas[1] & 0xFF)));
        if (log.isDebugEnabled()) {
            log.debug("Résultat de la lecture analogique du capteur {} = {}", capteurId, res);
        }
        return res;
    }
}
