package org.arig.eurobot.constants;

import com.pi4j.gpio.extension.pcf.PCF8574Pin;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;

/**
 * Created by mythril on 21/12/13.
 */
public interface IConstantesGPIO {

    // PCF Numérique Switch
    static final Pin N1_TIRETTE = PCF8574Pin.GPIO_06;
    static final Pin N1_BTN_TAPIS = PCF8574Pin.GPIO_07;
    static final Pin N1_SW_AVANT_GAUCHE = PCF8574Pin.GPIO_05;
    static final Pin N1_SW_AVANT_DROIT = PCF8574Pin.GPIO_04;
    static final Pin N1_SW_ARRIERE_GAUCHE = PCF8574Pin.GPIO_01;
    static final Pin N1_SW_ARRIERE_DROIT = PCF8574Pin.GPIO_00;
    static final Pin N1_SW_GB_GAUCHE = PCF8574Pin.GPIO_02;
    static final Pin N1_SW_GB_DROIT = PCF8574Pin.GPIO_03;

    // PCF Numérique Présence
    static final Pin N2_PRESENCE_GAUCHE = PCF8574Pin.GPIO_00;
    static final Pin N2_PRESENCE_CENTRE = PCF8574Pin.GPIO_02;
    static final Pin N2_PRESENCE_DROITE = PCF8574Pin.GPIO_01;

    // PCF Alimentation
    static final Pin ALIM_PUISSANCE_SERVO = PCF8574Pin.GPIO_00;
    static final Pin ALIM_PUISSANCE_MOTEUR = PCF8574Pin.GPIO_01;
    static final Pin ALIM_PUISSANCE_3 = PCF8574Pin.GPIO_02;
    static final Pin ALIM_AU = PCF8574Pin.GPIO_04;
    static final Pin ALIM_EN_PUISSANCE_SERVO = PCF8574Pin.GPIO_05;
    static final Pin ALIM_EN_PUISSANCE_MOTEUR = PCF8574Pin.GPIO_06;
    static final Pin ALIM_EN_PUISSANCE_3 = PCF8574Pin.GPIO_07;

    // IO Native
    static final Pin IRQ_ALIM = RaspiPin.GPIO_07;
    static final Pin IRQ_IRQ_1 = RaspiPin.GPIO_00;
    static final Pin IRQ_IRQ_2 = RaspiPin.GPIO_04;
    static final Pin IRQ_IRQ_3 = RaspiPin.GPIO_01;
    static final Pin IRQ_IRQ_4 = RaspiPin.GPIO_16;
    static final Pin IRQ_IRQ_5 = RaspiPin.GPIO_15;
    static final Pin IRQ_IRQ_6 = RaspiPin.GPIO_06;

    static final Pin CMD_LED_RGB = RaspiPin.GPIO_05;
    static final Pin EQUIPE = RaspiPin.GPIO_02;
    static final Pin PWM_R = RaspiPin.GPIO_12;
    static final Pin PWM_G = RaspiPin.GPIO_13;
    static final Pin PWM_B = RaspiPin.GPIO_14;
}
