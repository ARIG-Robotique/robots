package org.arig.robot.constants;

import com.pi4j.gpio.extension.pcf.PCF8574Pin;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;

/**
 * @author gdepuille on 21/12/13.
 */
public interface IConstantesGPIO {

    // ------- //
    // I2C ADC //
    // ------- //

    byte GP2D_AVANT_GAUCHE = 0;
    byte GP2D_AVANT_DROIT = 4;
    byte GP2D_AVANT_LATERAL_GAUCHE = 3;
    byte GP2D_AVANT_LATERAL_DROIT = 1;
    //byte GP2D_ARRIERE_GAUCHE = 6;
    //byte GP2D_ARRIERE_DROIT = 7;

    // ---------------- //
    // Expander PCF8574 //
    // ---------------- //

    // Numérique Switch
    Pin N1_TIRETTE = PCF8574Pin.GPIO_06;
    Pin N1_BTN_TAPIS = PCF8574Pin.GPIO_07;
    Pin N1_SW_AVANT_GAUCHE = PCF8574Pin.GPIO_05;
    Pin N1_SW_AVANT_DROIT = PCF8574Pin.GPIO_04;
    Pin N1_SW_ARRIERE_GAUCHE = PCF8574Pin.GPIO_01;
    Pin N1_SW_ARRIERE_DROIT = PCF8574Pin.GPIO_00;
    Pin N1_SW_GB_GAUCHE = PCF8574Pin.GPIO_02;
    Pin N1_SW_GB_DROIT = PCF8574Pin.GPIO_03;

    // Numérique Présence
    Pin N2_PRESENCE_GAUCHE = PCF8574Pin.GPIO_00;
    Pin N2_PRESENCE_CENTRE = PCF8574Pin.GPIO_02;
    Pin N2_PRESENCE_DROITE = PCF8574Pin.GPIO_01;

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
    Pin IRQ_1 = RaspiPin.GPIO_00;
    Pin IRQ_2 = RaspiPin.GPIO_04;
    Pin IRQ_3 = RaspiPin.GPIO_01;
    Pin IRQ_4 = RaspiPin.GPIO_16;
    Pin IRQ_5 = RaspiPin.GPIO_15;
    Pin IRQ_6 = RaspiPin.GPIO_06;
    Pin EQUIPE = RaspiPin.GPIO_02;

    // Outputs
    Pin CMD_LED_RGB = RaspiPin.GPIO_05;
    Pin PWM_R = RaspiPin.GPIO_12;
    Pin PWM_G = RaspiPin.GPIO_13;
    Pin PWM_B = RaspiPin.GPIO_14;
}
