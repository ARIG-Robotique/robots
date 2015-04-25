package org.arig.eurobot.services;

import com.pi4j.gpio.extension.pcf.PCF8574GpioProvider;
import com.pi4j.io.gpio.PinState;
import lombok.extern.slf4j.Slf4j;
import org.arig.eurobot.constants.IConstantesGPIO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Created by gdepuille on 23/04/15.
 */
@Slf4j
@Service
public class IOServices {

    @Autowired
    @Qualifier("pcfAlim")
    private PCF8574GpioProvider pcfAlim;

    public boolean auOk() {
        return PinState.getInverseState(pcfAlim.getState(IConstantesGPIO.ALIM_AU)) == PinState.HIGH;
    }

    public boolean alimServoOk() {
        return pcfAlim.getState(IConstantesGPIO.ALIM_EN_PUISSANCE_SERVO) == PinState.HIGH;
    }

    public boolean alimMoteurOk() {
        return pcfAlim.getState(IConstantesGPIO.ALIM_EN_PUISSANCE_MOTEUR) == PinState.HIGH;
    }

    public void enableAlimMoteur() {
        log.info("Activation puissance moteur");
        pcfAlim.setState(IConstantesGPIO.ALIM_PUISSANCE_MOTEUR, PinState.LOW);
    }
    public void disableAlimMoteur() {
        log.info("Desactivation puissance moteur");
        pcfAlim.setState(IConstantesGPIO.ALIM_PUISSANCE_MOTEUR, PinState.HIGH);
    }
    public void enableAlimServoMoteur() {
        log.info("Activation puissance servos-moteur");
        pcfAlim.setState(IConstantesGPIO.ALIM_PUISSANCE_SERVO, PinState.LOW);
    }
    public void disableAlimServoMoteur() {
        log.info("Desactivation puissance servos-moteur");
        pcfAlim.setState(IConstantesGPIO.ALIM_PUISSANCE_SERVO, PinState.HIGH);
    }
}
