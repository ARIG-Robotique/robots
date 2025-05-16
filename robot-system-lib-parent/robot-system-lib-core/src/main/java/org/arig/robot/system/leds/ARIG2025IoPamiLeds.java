package org.arig.robot.system.leds;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.i2c.I2CManager;
import org.arig.robot.exception.I2CException;

@Slf4j
public class ARIG2025IoPamiLeds {

    private static final byte LED_REGISTER = 'L';

    private LedColor ledAU;
    private LedColor ledTeam;
    private LedColor ledCalage;
    private LedColor ledCentrale;
    private LedColor ledAll;

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

    public ARIG2025IoPamiLeds(final I2CManager i2cManager) {
        this(i2cManager, "ARIG2025IoPamiLeds");
    }

    public ARIG2025IoPamiLeds(final I2CManager i2cManager, final String deviceName) {
        this.deviceName = deviceName;
        this.i2cManager = i2cManager;
    }

    public void setLedAU(LedColor color) {
        try {
            if (ledAU != color) {
                log.info("Led AU : {} -> {}", ledAU != null ? ledAU.name() : "UNKNOWN", color.name());
                ledAU = color;
                i2cManager.sendData(deviceName, LED_REGISTER, (byte) 1, (byte) color.colorCode);
                i2cManager.sendData(deviceName, LED_REGISTER, (byte) 7, (byte) color.colorCode);
            }
        } catch (I2CException e) {
            log.error("Erreur lors de l'affichage des LEDs AU");
        }
    }

    public void setLedTeam(LedColor color) {
        try {
            if (ledTeam != color) {
                log.info("Led Team : {} -> {}", ledTeam != null ? ledTeam.name() : "UNKNOWN", color.name());
                ledTeam = color;
                i2cManager.sendData(deviceName, LED_REGISTER, (byte) 2, (byte) color.colorCode);
                i2cManager.sendData(deviceName, LED_REGISTER, (byte) 6, (byte) color.colorCode);
            }
        } catch (I2CException e) {
            log.error("Erreur lors de l'affichage des LEDs Team");
        }
    }

    public void setLedCalage(LedColor color) {
        try {
            if (ledCalage != color) {
                log.info("Led Calage : {} -> {}", ledCalage != null ? ledCalage.name() : "UNKNOWN", color.name());
                ledCalage = color;
                i2cManager.sendData(deviceName, LED_REGISTER, (byte) 3, (byte) color.colorCode);
                i2cManager.sendData(deviceName, LED_REGISTER, (byte) 5, (byte) color.colorCode);
            }
        } catch (I2CException e) {
            log.error("Erreur lors de l'affichage des LEDs Calage");
        }
    }

    public void setLedCentrale(LedColor color) {
        try {
            if (ledCentrale != color) {
                log.info("Led centrale : {} -> {}", ledCentrale != null ? ledCentrale.name() : "UNKNOWN", color.name());
                ledCentrale = color;
                i2cManager.sendData(deviceName, LED_REGISTER, (byte) 4, (byte) color.colorCode);
            }
        } catch (I2CException e) {
            log.error("Erreur lors de l'affichage de la LED centrale");
        }
    }

    public void setAllLeds(LedColor color) {
        try {
            if (ledAll != color) {
                log.info("All Leds : {} -> {}", ledAll != null ? ledAll.name() : "UNKNOWN", color.name());
                ledAll = ledAU = ledTeam = ledCalage = ledCentrale = color;
                i2cManager.sendData(deviceName, LED_REGISTER, (byte) 0, (byte) color.colorCode);
            }
        } catch (I2CException e) {
            log.error("Erreur lors de l'affichage de toutes les LEDs");
        }
    }
}
