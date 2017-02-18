package org.arig.robot.config.spring;

import com.pi4j.gpio.extension.pcf.PCF8574GpioProvider;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.i2c.I2CBus;
import org.arig.robot.constants.IConstantesI2C;
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.arig.robot.monitoring.MonitoringJsonWrapper;
import org.arig.robot.services.IOService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.io.IOException;

/**
 * @author gdepuille on 11/04/17.
 */
@Configuration
@Import({ NerellUtilsI2CContext.class })
public class NerellUtilsCheckIOsContext {

    @Bean
    public IMonitoringWrapper monitoringWrapper() {
        return new MonitoringJsonWrapper();
    }

    @Bean(name = "ioRaspi", destroyMethod = "shutdown")
    public GpioController gpioController() {
        return GpioFactory.getInstance();
    }

    @Bean(name = "pcfAlim", destroyMethod = "shutdown")
    public PCF8574GpioProvider pcfAlim(I2CBus bus) throws IOException {
        return new PCF8574GpioProvider(bus, IConstantesI2C.PCF_ALIM_ADDRESS);
    }

    @Bean
    public IOService ioService() {
        return new IOService();
    }
}
