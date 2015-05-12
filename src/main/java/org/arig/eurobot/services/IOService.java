package org.arig.eurobot.services;

import com.pi4j.gpio.extension.pcf.PCF8574GpioProvider;
import com.pi4j.io.gpio.*;
import lombok.extern.slf4j.Slf4j;
import org.arig.eurobot.constants.IConstantesGPIO;
import org.arig.eurobot.model.RobotStatus;
import org.arig.eurobot.model.Team;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Created by gdepuille on 23/04/15.
 */
@Slf4j
@Service
public class IOService implements InitializingBean {

    @Autowired
    private RobotStatus rs;

    @Autowired
    @Qualifier("ioRaspi")
    private GpioController gpio;

    @Autowired
    @Qualifier("pcfAlim")
    private PCF8574GpioProvider pcfAlim;

    @Autowired
    @Qualifier("pcfSwitch")
    private PCF8574GpioProvider pcfSwitch;

    @Autowired
    @Qualifier("pcfPresence")
    private PCF8574GpioProvider pcfPresence;

    // Référence sur les PIN Input
    private GpioPinDigitalInput pinEquipe;

    // Référence sur les PIN Output
    private GpioPinDigitalOutput pinAlimPuissanceMoteur;
    private GpioPinDigitalOutput pinAlimPuissanceServosMoteur;
    private GpioPinDigitalOutput pinAlimPuissance3;
    private GpioPinDigitalOutput pinCmdLedCapteurRGB;
    private GpioPinDigitalOutput pinLedRGB_R;
    private GpioPinDigitalOutput pinLedRGB_G;
    private GpioPinDigitalOutput pinLedRGB_B;

    @Override
    public void afterPropertiesSet() throws Exception {
        // Config PCF8574 //
        // -------------- //

        // Switch
        pcfSwitch.setMode(IConstantesGPIO.N1_BTN_TAPIS, PinMode.DIGITAL_INPUT);
        pcfSwitch.setMode(IConstantesGPIO.N1_TIRETTE, PinMode.DIGITAL_INPUT);
        pcfSwitch.setMode(IConstantesGPIO.N1_SW_ARRIERE_DROIT, PinMode.DIGITAL_INPUT);
        pcfSwitch.setMode(IConstantesGPIO.N1_SW_ARRIERE_GAUCHE, PinMode.DIGITAL_INPUT);
        pcfSwitch.setMode(IConstantesGPIO.N1_SW_AVANT_DROIT, PinMode.DIGITAL_INPUT);
        pcfSwitch.setMode(IConstantesGPIO.N1_SW_AVANT_GAUCHE, PinMode.DIGITAL_INPUT);
        pcfSwitch.setMode(IConstantesGPIO.N1_SW_GB_DROIT, PinMode.DIGITAL_INPUT);
        pcfSwitch.setMode(IConstantesGPIO.N1_SW_GB_GAUCHE, PinMode.DIGITAL_INPUT);

        // Présence
        pcfPresence.setMode(IConstantesGPIO.N2_PRESENCE_CENTRE, PinMode.DIGITAL_INPUT);
        pcfPresence.setMode(IConstantesGPIO.N2_PRESENCE_DROITE, PinMode.DIGITAL_INPUT);
        pcfPresence.setMode(IConstantesGPIO.N2_PRESENCE_GAUCHE, PinMode.DIGITAL_INPUT);

        // Alim
        pcfAlim.setMode(IConstantesGPIO.ALIM_AU, PinMode.DIGITAL_INPUT);
        pcfAlim.setMode(IConstantesGPIO.ALIM_EN_PUISSANCE_MOTEUR, PinMode.DIGITAL_INPUT);
        pcfAlim.setMode(IConstantesGPIO.ALIM_EN_PUISSANCE_SERVO, PinMode.DIGITAL_INPUT);
        pcfAlim.setMode(IConstantesGPIO.ALIM_EN_PUISSANCE_3, PinMode.DIGITAL_INPUT);

        pinAlimPuissanceMoteur = gpio.provisionDigitalOutputPin(pcfAlim, IConstantesGPIO.ALIM_PUISSANCE_MOTEUR);
        pinAlimPuissanceServosMoteur = gpio.provisionDigitalOutputPin(pcfAlim, IConstantesGPIO.ALIM_PUISSANCE_SERVO);
        pinAlimPuissance3 = gpio.provisionDigitalOutputPin(pcfAlim, IConstantesGPIO.ALIM_PUISSANCE_3);

        // Config des IO raspi //
        // ------------------- //

        // Inputs
        pinEquipe = gpio.provisionDigitalInputPin(IConstantesGPIO.EQUIPE);
        gpio.provisionDigitalInputPin(IConstantesGPIO.IRQ_ALIM);
        gpio.provisionDigitalInputPin(IConstantesGPIO.IRQ_1);
        gpio.provisionDigitalInputPin(IConstantesGPIO.IRQ_2);
        gpio.provisionDigitalInputPin(IConstantesGPIO.IRQ_3);
        gpio.provisionDigitalInputPin(IConstantesGPIO.IRQ_4);
        gpio.provisionDigitalInputPin(IConstantesGPIO.IRQ_5);
        gpio.provisionDigitalInputPin(IConstantesGPIO.IRQ_6);

        // Output
        pinCmdLedCapteurRGB = gpio.provisionDigitalOutputPin(IConstantesGPIO.CMD_LED_RGB, PinState.LOW);
        pinLedRGB_R = gpio.provisionDigitalOutputPin(IConstantesGPIO.PWM_R);
        pinLedRGB_G = gpio.provisionDigitalOutputPin(IConstantesGPIO.PWM_G);
        pinLedRGB_B = gpio.provisionDigitalOutputPin(IConstantesGPIO.PWM_B);

        // On éteint la led du capteur, elle pique les yeux.
        pinCmdLedCapteurRGB.low();
        pinLedRGB_R.low();
        pinLedRGB_G.low();
        pinLedRGB_B.low();
    }

    // --------------------------------------------------------- //
    // --------------------- CHECK PREPARATION ----------------- //
    // --------------------------------------------------------- //

    public boolean btnTapis() {
        return pcfSwitch.getState(IConstantesGPIO.N1_BTN_TAPIS) == PinState.LOW;
    }

    public Team equipe() {
        rs.setTeam(pinEquipe.isHigh() ? Team.JAUNE : Team.VERT);
        if (rs.getTeam() == Team.JAUNE) {
            pinLedRGB_R.high();
            pinLedRGB_G.high();
            pinLedRGB_B.low();
        } else {
            pinLedRGB_R.low();
            pinLedRGB_G.high();
            pinLedRGB_B.low();
        }

        return rs.getTeam();
    }

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

    public boolean produitGauche() {
        return piedGauche() || gobeletGauche();
    }

    public boolean gobeletGauche() {
        return pcfSwitch.getState(IConstantesGPIO.N1_SW_GB_GAUCHE) == PinState.LOW;
    }

    public boolean piedGauche() {
        return pcfPresence.getState(IConstantesGPIO.N2_PRESENCE_GAUCHE) == PinState.LOW && !gobeletGauche();
    }

    public boolean produitDroit() {
        return piedDroit() || gobeletDroit();
    }

    public boolean gobeletDroit() {
        return pcfSwitch.getState(IConstantesGPIO.N1_SW_GB_DROIT) == PinState.LOW;
    }

    public boolean piedDroit() {
        return pcfPresence.getState(IConstantesGPIO.N2_PRESENCE_DROITE) == PinState.LOW && !gobeletDroit();
    }

    public boolean piedCentre() {
        return pcfPresence.getState(IConstantesGPIO.N2_PRESENCE_CENTRE) == PinState.LOW;
    }

    // --------------------------------------------------------- //
    // -------------------------- OUTPUT ----------------------- //
    // --------------------------------------------------------- //

    public void colorAUKo() {
        pinLedRGB_R.high();
        pinLedRGB_G.low();
        pinLedRGB_B.low();
    }

    public void clearTeamColor() {
        pinLedRGB_R.low();
        pinLedRGB_G.low();
        pinLedRGB_B.low();
    }

    public void enableAlimMoteur() {
        log.info("Activation puissance moteur");
        pinAlimPuissanceMoteur.low();
    }
    public void disableAlimMoteur() {
        log.info("Desactivation puissance moteur");
        pinAlimPuissanceMoteur.high();
    }
    public void enableAlimServoMoteur() {
        log.info("Activation puissance servos-moteur");
        pinAlimPuissanceServosMoteur.low();
    }
    public void disableAlimServoMoteur() {
        log.info("Desactivation puissance servos-moteur");
        pinAlimPuissanceServosMoteur.high();
    }
}
