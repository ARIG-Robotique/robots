package org.arig.robot.config.spring;

import com.pi4j.gpio.extension.pca.PCA9685GpioProvider;
import com.pi4j.gpio.extension.pca.PCA9685Pin;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinPwmOutput;
import com.pi4j.io.i2c.I2CBus;
import lombok.SneakyThrows;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.shell.jline.PromptProvider;

import java.math.BigDecimal;

@Configuration
@ComponentScan("org.arig.robot.nerell.utils")
public class NerellUtilsShellContext {

    @Bean
    public PromptProvider myPromptProvider() {
        return () -> new AttributedString("nerell-utils:> ", AttributedStyle.DEFAULT.background(AttributedStyle.YELLOW));
    }

    @SneakyThrows
    @Bean
    public PCA9685GpioProvider pca9685GpioControler(I2CBus bus) {
        final PCA9685GpioProvider gpioProvider = new PCA9685GpioProvider(bus, 0x40, new BigDecimal(100));

        final GpioController gpio = GpioFactory.getInstance();
/*
        gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_00);
        gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_01);
        gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_02);
        gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_03);
        gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_04);
        gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_05);
        gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_06);
        gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_07);
        gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_08);
        gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_09);
        gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_10);
        gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_11);
*/
        gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_12);
        gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_13);
        gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_14);
        gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_15);

        return gpioProvider;
    }
}
