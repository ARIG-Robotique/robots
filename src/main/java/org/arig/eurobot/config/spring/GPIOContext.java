package org.arig.eurobot.config.spring;

import com.pi4j.gpio.extension.pcf.PCF8574GpioProvider;
import com.pi4j.io.gpio.*;
import com.pi4j.io.i2c.I2CBus;
import org.arig.eurobot.constants.IConstantesGPIO;
import org.arig.eurobot.constants.IConstantesI2C;
import org.arig.robot.system.capteurs.IDigitalInputCapteurs;
import org.arig.robot.system.capteurs.RaspiBoard2007NoMux;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;

/**
 * Created by mythril on 21/12/13.
 */
@Configuration
@Profile("raspi")
public class GPIOContext {

    @Bean(destroyMethod = "shutdown")
    public GpioController gpioController() {
        return GpioFactory.getInstance();
    }

    @Bean(name = "capteursSwitch", destroyMethod = "shutdown")
    public PCF8574GpioProvider capteursSwitch(I2CBus bus) throws IOException {
        PCF8574GpioProvider c = new PCF8574GpioProvider(bus, IConstantesI2C.PCF_NUM1_ADDRESS);
        c.setMode(IConstantesGPIO.N1_BTN_TAPIS, PinMode.DIGITAL_INPUT);
        c.setMode(IConstantesGPIO.N1_TIRETTE, PinMode.DIGITAL_INPUT);
        c.setMode(IConstantesGPIO.N1_SW_ARRIERE_DROIT, PinMode.DIGITAL_INPUT);
        c.setMode(IConstantesGPIO.N1_SW_ARRIERE_GAUCHE, PinMode.DIGITAL_INPUT);
        c.setMode(IConstantesGPIO.N1_SW_AVANT_DROIT, PinMode.DIGITAL_INPUT);
        c.setMode(IConstantesGPIO.N1_SW_AVANT_GAUCHE, PinMode.DIGITAL_INPUT);
        c.setMode(IConstantesGPIO.N1_SW_GB_DROIT, PinMode.DIGITAL_INPUT);
        c.setMode(IConstantesGPIO.N1_SW_GB_GAUCHE, PinMode.DIGITAL_INPUT);
        return c;
    }

    @Bean(name = "capteursPresence", destroyMethod = "shutdown")
    public PCF8574GpioProvider capteursPresence(I2CBus bus) throws IOException {
        PCF8574GpioProvider c = new PCF8574GpioProvider(bus, IConstantesI2C.PCF_NUM2_ADDRESS);
        c.setMode(IConstantesGPIO.N2_PRESENCE_CENTRE, PinMode.DIGITAL_INPUT);
        c.setMode(IConstantesGPIO.N2_PRESENCE_DROITE, PinMode.DIGITAL_INPUT);
        c.setMode(IConstantesGPIO.N2_PRESENCE_GAUCHE, PinMode.DIGITAL_INPUT);
        return c;
    }

    @Bean(name = "capteursAlim", destroyMethod = "shutdown")
    public PCF8574GpioProvider capteursAlim(I2CBus bus) throws IOException {
        PCF8574GpioProvider c = new PCF8574GpioProvider(bus, IConstantesI2C.PCF_ALIM_ADDRESS);
        c.setMode(IConstantesGPIO.ALIM_PUISSANCE_MOTEUR, PinMode.DIGITAL_OUTPUT);
        c.setMode(IConstantesGPIO.ALIM_PUISSANCE_SERVO, PinMode.DIGITAL_OUTPUT);
        return c;
    }
}
