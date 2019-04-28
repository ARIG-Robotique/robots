package org.arig.robot.constants;

import org.arig.pi4j.gpio.extension.pcf.PCF8574GpioProvider;
import org.arig.robot.system.capteurs.TCS34725ColorSensor;

/**
 * @author gdepuille on 21/12/13.
 */
public interface IConstantesI2C {

    String CODEUR_MOTEUR_DROIT = "Codeur Moteur Droit";
    int CODEUR_DROIT_ADDRESS = 0x30;

    String CODEUR_MOTEUR_GAUCHE = "Codeur Moteur Gauche";
    int CODEUR_GAUCHE_ADDRESS = 0x32;

    String CODEUR_MOTEUR_CAROUSEL = "Codeur Moteur Carousel";
    int CODEUR_CAROUSEL_ADDRESS = 0x34;

    String PCF_ALIM_DEVICE_NAME = "Carte alimentation";
    int PCF_ALIM_ADDRESS = PCF8574GpioProvider.PCF8574A_0x3D;

    String PCF1_DEVICE_NAME = "PCF 1 (Input)";
    int PCF1_ADDRESS = PCF8574GpioProvider.PCF8574A_0x3B;

    String PCF2_DEVICE_NAME = "PCF 2 (Input)";
    int PCF2_ADDRESS = PCF8574GpioProvider.PCF8574A_0x3E;

    String PCF3_DEVICE_NAME = "PCF 3 (Output)";
    int PCF3_ADDRESS = PCF8574GpioProvider.PCF8574A_0x3F;

    String I2C_ADC_DEVICE_NAME = "I2C ADC";
    int I2C_ADC_ADDRESS = 0x48;

    String SERVO_DEVICE_NAME = "SD21";
    int SD21_ADDRESS = 0x61;

    String TCS34725_DEVICE_NAME = "Front color sensor";
    int TCS34725_ADDRESS = TCS34725ColorSensor.TCS34725_ADDRESS;

    String TINY_LIDAR_MAGASIN_DROIT_DEVICE_NAME = "tinyLidar magasin droit";
    int TINY_LIDAR_MAGASIN_DROIT_ADDRESS = 0x14;

    String TINY_LIDAR_MAGASIN_GAUCHE_DEVICE_NAME = "tinyLidar magasin gauche";
    int TINY_LIDAR_MAGASIN_GAUCHE_ADDRESS = 0x13;

    String TINY_LIDAR_AVANT_DROIT_DEVICE_NAME = "tinyLidar avant droit";
    int TINY_LIDAR_AVANT_DROIT_ADDRESS = 0x11;

    String TINY_LIDAR_AVANT_GAUCHE_DEVICE_NAME = "tinyLidar avant gauche";
    int TINY_LIDAR_AVANT_GAUCHE_ADDRESS = 0x12;

}
