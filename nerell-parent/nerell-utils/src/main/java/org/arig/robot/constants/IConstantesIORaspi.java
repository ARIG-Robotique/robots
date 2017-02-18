package org.arig.robot.constants;

import com.pi4j.gpio.extension.pcf.PCF8574Pin;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;

/**
 * @author gdepuille on 21/12/13.
 */
public interface IConstantesIORaspi {

    // ---------------- //
    // Expander PCF8574 //
    // ---------------- //

    // Alimentation
    Pin ALIM_PUISSANCE_SERVO = PCF8574Pin.GPIO_00;
    Pin ALIM_PUISSANCE_MOTEUR = PCF8574Pin.GPIO_01;
    Pin ALIM_PUISSANCE_3 = PCF8574Pin.GPIO_02;
    Pin ALIM_AU = PCF8574Pin.GPIO_04;
    Pin ALIM_EN_PUISSANCE_SERVO = PCF8574Pin.GPIO_05;
    Pin ALIM_EN_PUISSANCE_MOTEUR = PCF8574Pin.GPIO_06;
    Pin ALIM_EN_PUISSANCE_3 = PCF8574Pin.GPIO_07;

    // ------------------------------- //
    // Input / Output native Raspberry //
    // ------------------------------- //

    // Inputs
    Pin IRQ_ALIM = RaspiPin.GPIO_07;

    // Outputs
    Pin CMD_LED_CAPTEUR_RGB = RaspiPin.GPIO_05;
    Pin PWM_R = RaspiPin.GPIO_12;
    Pin PWM_G = RaspiPin.GPIO_13;
    Pin PWM_B = RaspiPin.GPIO_14;
}
