package org.arig.robot.config.spring;

import com.pi4j.gpio.extension.pcf.PCF8574GpioProvider;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.i2c.I2CBus;
import org.arig.robot.constants.IConstantesI2C;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @author gdepuille on 21/12/13.
 */
@Configuration
public class NerellGpioContext {

    @Bean(name = "ioRaspi", destroyMethod = "shutdown")
    public GpioController gpioController() {
        return GpioFactory.getInstance();
    }

    @Bean(name = "pcfSwitch", destroyMethod = "shutdown")
    public PCF8574GpioProvider pcfSwitch(I2CBus bus) throws IOException {
        return new PCF8574GpioProvider(bus, IConstantesI2C.PCF_NUM1_ADDRESS);
    }

    @Bean(name = "pcfPresence", destroyMethod = "shutdown")
    public PCF8574GpioProvider pcfPresence(I2CBus bus) throws IOException {
        return new PCF8574GpioProvider(bus, IConstantesI2C.PCF_NUM2_ADDRESS);
    }

    @Bean(name = "pcfAlim", destroyMethod = "shutdown")
    public PCF8574GpioProvider pcfAlim(I2CBus bus) throws IOException {
        return new PCF8574GpioProvider(bus, IConstantesI2C.PCF_ALIM_ADDRESS);
    }
}
