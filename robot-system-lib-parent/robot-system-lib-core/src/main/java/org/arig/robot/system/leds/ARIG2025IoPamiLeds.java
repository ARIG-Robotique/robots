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

        @Accessors(fluent = true)
        private final char colorCode;
    }

    @RequiredArgsConstructor
    public enum LedId {
        ALL_LEDS((byte) 0),
        LED1((byte) 1),
        LED2((byte) 2),
        LED3((byte) 3),
        LED4((byte) 4),
        LED5((byte) 5),
        LED6((byte) 6),
        LED7((byte) 7);

        @Accessors(fluent = true)
        private final byte id;
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

    public void setLedColor(LedId led, LedColor color) {
        // Reset led color
        ledAll = ledAU = ledTeam = ledCalage = ledCentrale = null;

        try {
            i2cManager.sendData(deviceName, LED_REGISTER, led.id, (byte) color.colorCode);
        } catch (I2CException e) {
            log.error("Erreur lors de l'affichage des LEDs AU");
        }
    }

    public void setLedAU(LedColor color) {
        try {
            if (ledAU != color) {
                log.info("Led AU : {} -> {}", ledAU != null ? ledAU.name() : "UNKNOWN", color.name());
                ledAU = color;
                i2cManager.sendData(deviceName, LED_REGISTER, LedId.LED1.id, (byte) color.colorCode);
                i2cManager.sendData(deviceName, LED_REGISTER, LedId.LED7.id, (byte) color.colorCode);
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
                i2cManager.sendData(deviceName, LED_REGISTER, LedId.LED2.id, (byte) color.colorCode);
                i2cManager.sendData(deviceName, LED_REGISTER, LedId.LED6.id, (byte) color.colorCode);
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
                i2cManager.sendData(deviceName, LED_REGISTER, LedId.LED3.id, (byte) color.colorCode);
                i2cManager.sendData(deviceName, LED_REGISTER, LedId.LED5.id, (byte) color.colorCode);
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
                i2cManager.sendData(deviceName, LED_REGISTER, LedId.LED4.id, (byte) color.colorCode);
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
                i2cManager.sendData(deviceName, LED_REGISTER, LedId.ALL_LEDS.id, (byte) color.colorCode);
            }
        } catch (I2CException e) {
            log.error("Erreur lors de l'affichage de toutes les LEDs");
        }
    }
}
