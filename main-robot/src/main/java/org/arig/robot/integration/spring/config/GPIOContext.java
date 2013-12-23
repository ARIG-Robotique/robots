package org.arig.robot.integration.spring.config;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;
import org.arig.robot.system.capteurs.IDigitalInputCapteurs;
import org.arig.robot.system.capteurs.RaspiBoard2007NoMux;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

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

    @Bean
    public IDigitalInputCapteurs<Pin> capteurs() {
        RaspiBoard2007NoMux c = new RaspiBoard2007NoMux(gpioController());
        c.setInputPinForCapteur(RaspiBoard2007NoMux.CapteursDefinition.EQUIPE.getId(), RaspiPin.GPIO_14);

        return c;
    }
}
