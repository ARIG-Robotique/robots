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

    @Autowired
    @Qualifier("pcfSwitch")
    private PCF8574GpioProvider pcfSwitch;

    @Autowired
    @Qualifier("pcfPresence")
    private PCF8574GpioProvider pcfPresence;

    // --------------------------------------------------------- //
    // -------------------------- INPUT ------------------------ //
    // --------------------------------------------------------- //

    public boolean auOk() {
        return pcfAlim.getState(IConstantesGPIO.ALIM_AU) == PinState.LOW;
    }

    public boolean alimServoOk() {
        return pcfAlim.getState(IConstantesGPIO.ALIM_EN_PUISSANCE_SERVO) == PinState.HIGH;
    }

    public boolean alimMoteurOk() {
        return pcfAlim.getState(IConstantesGPIO.ALIM_EN_PUISSANCE_MOTEUR) == PinState.HIGH;
    }

    public boolean tirette() {
        return pcfSwitch.getState(IConstantesGPIO.N1_TIRETTE) == PinState.LOW;
    }

    public boolean btnTapis() {
        return pcfSwitch.getState(IConstantesGPIO.N1_BTN_TAPIS) == PinState.LOW;
    }

    public boolean buteeAvantGauche() {
        return pcfSwitch.getState(IConstantesGPIO.N1_SW_AVANT_GAUCHE) == PinState.LOW;
    }

    public boolean buteeAvantDroit() {
        return pcfSwitch.getState(IConstantesGPIO.N1_SW_AVANT_DROIT) == PinState.LOW;
    }

    public boolean buteeArriereGauche() {
        return pcfSwitch.getState(IConstantesGPIO.N1_SW_ARRIERE_GAUCHE) == PinState.LOW;
    }

    public boolean buteeArriereDroit() {
        return pcfSwitch.getState(IConstantesGPIO.N1_SW_ARRIERE_DROIT) == PinState.LOW;
    }

    public boolean gobeletGauche() {
        return pcfPresence.getState(IConstantesGPIO.N2_PRESENCE_GAUCHE) == PinState.LOW
                && pcfSwitch.getState(IConstantesGPIO.N1_SW_GB_GAUCHE) == PinState.LOW;
    }

    public boolean piedGauche() {
        return pcfPresence.getState(IConstantesGPIO.N2_PRESENCE_GAUCHE) == PinState.LOW
                && pcfSwitch.getState(IConstantesGPIO.N1_SW_GB_GAUCHE) == PinState.HIGH;
    }

    public boolean gobeletDroit() {
        return pcfPresence.getState(IConstantesGPIO.N2_PRESENCE_DROITE) == PinState.LOW
                && pcfSwitch.getState(IConstantesGPIO.N1_SW_GB_DROIT) == PinState.LOW;
    }

    public boolean piedDroit() {
        return pcfPresence.getState(IConstantesGPIO.N2_PRESENCE_DROITE) == PinState.LOW
                && pcfSwitch.getState(IConstantesGPIO.N1_SW_GB_DROIT) == PinState.HIGH;
    }

    public boolean piedCentre() {
        return pcfPresence.getState(IConstantesGPIO.N2_PRESENCE_CENTRE) == PinState.LOW;
    }

    // --------------------------------------------------------- //
    // -------------------------- OUTPUT ----------------------- //
    // --------------------------------------------------------- //

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
