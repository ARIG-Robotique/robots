package org.arig.robot.system.leds;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.i2c.I2CManager;
import org.arig.robot.exception.I2CException;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class ARIG2024IoPamiLeds {

    private static final byte LED_REGISTER = 'L';

    @RequiredArgsConstructor
    public enum LedColor {
        White('W'),
        Red('R'),
        Green('G'),
        Blue('B'),
        Yellow('Y'),
        Black('K');

        @Getter
        @Accessors(fluent = true)
        private final char colorCode;
    }

    @Getter
    @Accessors(fluent = true)
    private final String deviceName;

    private final I2CManager i2cManager;

    public ARIG2024IoPamiLeds(final I2CManager i2cManager) {
        this(i2cManager, "ARIG2024IoPamiLeds");
    }

    public ARIG2024IoPamiLeds(final I2CManager i2cManager, final String deviceName) {
        this.deviceName = deviceName;
        this.i2cManager = i2cManager;
    }

    public void setLedAU(LedColor color) {
        try {
            log.info("Led AU : {}", color.name());
            i2cManager.sendData(deviceName, LED_REGISTER, (byte) 1, (byte) color.colorCode);
        } catch (I2CException e) {
            log.error("Erreur lors de la définition de la vitesse et de la position");
        }
    }

    public void setLedTeam(LedColor color) {
        try {
            log.info("Led Team : {}", color.name());
            i2cManager.sendData(deviceName, LED_REGISTER, (byte) 2, (byte) color.colorCode);
        } catch (I2CException e) {
            log.error("Erreur lors de la définition de la vitesse et de la position");
        }
    }

    public void setLedCalage(LedColor color) {
        try {
            log.info("Led Calage : {}", color.name());
            i2cManager.sendData(deviceName, LED_REGISTER, (byte) 3, (byte) color.colorCode);
        } catch (I2CException e) {
            log.error("Erreur lors de la définition de la vitesse et de la position");
        }
    }

    public void setAllLeds(LedColor color) {
        try {
            log.info("All Leds : {}", color.name());
            i2cManager.sendData(deviceName, LED_REGISTER, (byte) 0, (byte) color.colorCode);
        } catch (I2CException e) {
            log.error("Erreur lors de la définition de la vitesse et de la position");
        }
    }
}
