package org.arig.eurobot.constants;

import com.pi4j.gpio.extension.pcf.PCF8574Pin;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;

/**
 * Created by mythril on 21/12/13.
 */
public interface IConstantesGPIO {

    // ------- //
    // I2C ADC //
    // ------- //

    static final byte GP2D_AVANT_GAUCHE = 0;
    static final byte GP2D_AVANT_DROIT = 4;
    static final byte GP2D_AVANT_LATERAL_GAUCHE = 3;
    static final byte GP2D_AVANT_LATERAL_DROIT = 1;
    //static final byte GP2D_ARRIERE_GAUCHE = 6;
    //static final byte GP2D_ARRIERE_DROIT = 7;

    // ---------------- //
    // Expander PCF8574 //
    // ---------------- //

    // Numérique Switch
    static final Pin N1_TIRETTE = PCF8574Pin.GPIO_06;
    static final Pin N1_BTN_TAPIS = PCF8574Pin.GPIO_07;
    static final Pin N1_SW_AVANT_GAUCHE = PCF8574Pin.GPIO_05;
    static final Pin N1_SW_AVANT_DROIT = PCF8574Pin.GPIO_04;
    static final Pin N1_SW_ARRIERE_GAUCHE = PCF8574Pin.GPIO_01;
    static final Pin N1_SW_ARRIERE_DROIT = PCF8574Pin.GPIO_00;
    static final Pin N1_SW_GB_GAUCHE = PCF8574Pin.GPIO_02;
    static final Pin N1_SW_GB_DROIT = PCF8574Pin.GPIO_03;

    // Numérique Présence
    static final Pin N2_PRESENCE_GAUCHE = PCF8574Pin.GPIO_00;
    static final Pin N2_PRESENCE_CENTRE = PCF8574Pin.GPIO_02;
    static final Pin N2_PRESENCE_DROITE = PCF8574Pin.GPIO_01;

    // Alimentation
    static final Pin ALIM_PUISSANCE_SERVO = PCF8574Pin.GPIO_00;
    static final Pin ALIM_PUISSANCE_MOTEUR = PCF8574Pin.GPIO_01;
    static final Pin ALIM_PUISSANCE_3 = PCF8574Pin.GPIO_02;
    static final Pin ALIM_AU = PCF8574Pin.GPIO_04;
    static final Pin ALIM_EN_PUISSANCE_SERVO = PCF8574Pin.GPIO_05;
    static final Pin ALIM_EN_PUISSANCE_MOTEUR = PCF8574Pin.GPIO_06;
    static final Pin ALIM_EN_PUISSANCE_3 = PCF8574Pin.GPIO_07;

    // ------------------------------- //
    // Input / Output native Raspberry //
    // ------------------------------- //

    // Inputs
    static final Pin IRQ_ALIM = RaspiPin.GPIO_07;
    static final Pin IRQ_1 = RaspiPin.GPIO_00;
    static final Pin IRQ_2 = RaspiPin.GPIO_04;
    static final Pin IRQ_3 = RaspiPin.GPIO_01;
    static final Pin IRQ_4 = RaspiPin.GPIO_16;
    static final Pin IRQ_5 = RaspiPin.GPIO_15;
    static final Pin IRQ_6 = RaspiPin.GPIO_06;
    static final Pin EQUIPE = RaspiPin.GPIO_02;

    // Outputs
    static final Pin CMD_LED_RGB = RaspiPin.GPIO_05;
    static final Pin PWM_R = RaspiPin.GPIO_12;
    static final Pin PWM_G = RaspiPin.GPIO_13;
    static final Pin PWM_B = RaspiPin.GPIO_14;
}
