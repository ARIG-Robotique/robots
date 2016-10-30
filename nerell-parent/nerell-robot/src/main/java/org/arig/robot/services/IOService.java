package org.arig.robot.services;

import com.pi4j.gpio.extension.pcf.PCF8574GpioProvider;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.PinState;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesIORaspi;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.Team;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author gdepuille on 23/04/15.
 */
@Slf4j
@Service
public class IOService implements IIOService, InitializingBean {

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
        pcfSwitch.setMode(IConstantesIORaspi.N1_BTN_TAPIS, PinMode.DIGITAL_INPUT);
        pcfSwitch.setMode(IConstantesIORaspi.N1_TIRETTE, PinMode.DIGITAL_INPUT);
        pcfSwitch.setMode(IConstantesIORaspi.N1_SW_ARRIERE_DROIT, PinMode.DIGITAL_INPUT);
        pcfSwitch.setMode(IConstantesIORaspi.N1_SW_ARRIERE_GAUCHE, PinMode.DIGITAL_INPUT);
        pcfSwitch.setMode(IConstantesIORaspi.N1_SW_AVANT_DROIT, PinMode.DIGITAL_INPUT);
        pcfSwitch.setMode(IConstantesIORaspi.N1_SW_AVANT_GAUCHE, PinMode.DIGITAL_INPUT);
        pcfSwitch.setMode(IConstantesIORaspi.N1_SW_GB_DROIT, PinMode.DIGITAL_INPUT);
        pcfSwitch.setMode(IConstantesIORaspi.N1_SW_GB_GAUCHE, PinMode.DIGITAL_INPUT);

        // Présence
        pcfPresence.setMode(IConstantesIORaspi.N2_PRESENCE_CENTRE, PinMode.DIGITAL_INPUT);
        pcfPresence.setMode(IConstantesIORaspi.N2_PRESENCE_DROITE, PinMode.DIGITAL_INPUT);
        pcfPresence.setMode(IConstantesIORaspi.N2_PRESENCE_GAUCHE, PinMode.DIGITAL_INPUT);

        // Alim
        pcfAlim.setMode(IConstantesIORaspi.ALIM_AU, PinMode.DIGITAL_INPUT);
        pcfAlim.setMode(IConstantesIORaspi.ALIM_EN_PUISSANCE_MOTEUR, PinMode.DIGITAL_INPUT);
        pcfAlim.setMode(IConstantesIORaspi.ALIM_EN_PUISSANCE_SERVO, PinMode.DIGITAL_INPUT);
        pcfAlim.setMode(IConstantesIORaspi.ALIM_EN_PUISSANCE_3, PinMode.DIGITAL_INPUT);

        pinAlimPuissanceMoteur = gpio.provisionDigitalOutputPin(pcfAlim, IConstantesIORaspi.ALIM_PUISSANCE_MOTEUR);
        pinAlimPuissanceServosMoteur = gpio.provisionDigitalOutputPin(pcfAlim, IConstantesIORaspi.ALIM_PUISSANCE_SERVO);
        pinAlimPuissance3 = gpio.provisionDigitalOutputPin(pcfAlim, IConstantesIORaspi.ALIM_PUISSANCE_3);

        // Config des IO raspi //
        // ------------------- //

        // Inputs
        pinEquipe = gpio.provisionDigitalInputPin(IConstantesIORaspi.EQUIPE);
        gpio.provisionDigitalInputPin(IConstantesIORaspi.IRQ_ALIM);
        gpio.provisionDigitalInputPin(IConstantesIORaspi.IRQ_1);
        gpio.provisionDigitalInputPin(IConstantesIORaspi.IRQ_2);
        gpio.provisionDigitalInputPin(IConstantesIORaspi.IRQ_3);
        gpio.provisionDigitalInputPin(IConstantesIORaspi.IRQ_4);
        gpio.provisionDigitalInputPin(IConstantesIORaspi.IRQ_5);
        gpio.provisionDigitalInputPin(IConstantesIORaspi.IRQ_6);

        // Output
        pinCmdLedCapteurRGB = gpio.provisionDigitalOutputPin(IConstantesIORaspi.CMD_LED_RGB, PinState.LOW);
        pinLedRGB_R = gpio.provisionDigitalOutputPin(IConstantesIORaspi.PWM_R);
        pinLedRGB_G = gpio.provisionDigitalOutputPin(IConstantesIORaspi.PWM_G);
        pinLedRGB_B = gpio.provisionDigitalOutputPin(IConstantesIORaspi.PWM_B);

        // On éteint la led du capteur, elle pique les yeux.
        pinCmdLedCapteurRGB.low();
        pinLedRGB_R.low();
        pinLedRGB_G.low();
        pinLedRGB_B.low();
    }

    // --------------------------------------------------------- //
    // --------------------- CHECK PREPARATION ----------------- //
    // --------------------------------------------------------- //

    @Override
    public boolean btnTapis() {
        return pcfSwitch.getState(IConstantesIORaspi.N1_BTN_TAPIS) == PinState.LOW;
    }

    @Override
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

    @Override
    public boolean auOk() {
        return pcfAlim.getState(IConstantesIORaspi.ALIM_AU) == PinState.LOW;
    }

    @Override
    public boolean alimServoOk() {
        return pcfAlim.getState(IConstantesIORaspi.ALIM_EN_PUISSANCE_SERVO) == PinState.HIGH;
    }

    @Override
    public boolean alimMoteurOk() {
        return pcfAlim.getState(IConstantesIORaspi.ALIM_EN_PUISSANCE_MOTEUR) == PinState.HIGH;
    }

    @Override
    public boolean tirette() {
        return pcfSwitch.getState(IConstantesIORaspi.N1_TIRETTE) == PinState.LOW;
    }

    @Override
    public boolean buteeAvantGauche() {
        return pcfSwitch.getState(IConstantesIORaspi.N1_SW_AVANT_GAUCHE) == PinState.LOW;
    }

    @Override
    public boolean buteeAvantDroit() {
        return pcfSwitch.getState(IConstantesIORaspi.N1_SW_AVANT_DROIT) == PinState.LOW;
    }

    @Override
    public boolean buteeArriereGauche() {
        return pcfSwitch.getState(IConstantesIORaspi.N1_SW_ARRIERE_GAUCHE) == PinState.LOW;
    }

    @Override
    public boolean buteeArriereDroit() {
        return pcfSwitch.getState(IConstantesIORaspi.N1_SW_ARRIERE_DROIT) == PinState.LOW;
    }

    @Override
    public boolean produitGauche() {
        return piedGauche() || gobeletGauche();
    }

    @Override
    public boolean gobeletGauche() {
        return pcfSwitch.getState(IConstantesIORaspi.N1_SW_GB_GAUCHE) == PinState.LOW;
    }

    @Override
    public boolean piedGauche() {
        return pcfPresence.getState(IConstantesIORaspi.N2_PRESENCE_GAUCHE) == PinState.LOW && !gobeletGauche();
    }

    @Override
    public boolean produitDroit() {
        return piedDroit() || gobeletDroit();
    }

    @Override
    public boolean gobeletDroit() {
        return pcfSwitch.getState(IConstantesIORaspi.N1_SW_GB_DROIT) == PinState.LOW;
    }

    @Override
    public boolean piedDroit() {
        return pcfPresence.getState(IConstantesIORaspi.N2_PRESENCE_DROITE) == PinState.LOW && !gobeletDroit();
    }

    @Override
    public boolean piedCentre() {
        return pcfPresence.getState(IConstantesIORaspi.N2_PRESENCE_CENTRE) == PinState.LOW;
    }

    // --------------------------------------------------------- //
    // -------------------------- OUTPUT ----------------------- //
    // --------------------------------------------------------- //

    @Override
    public void colorAUKo() {
        pinLedRGB_R.high();
        pinLedRGB_G.low();
        pinLedRGB_B.low();
    }

    @Override
    public void clearTeamColor() {
        pinLedRGB_R.low();
        pinLedRGB_G.low();
        pinLedRGB_B.low();
    }

    @Override
    public void enableAlimMoteur() {
        log.info("Activation puissance moteur");
        pinAlimPuissanceMoteur.low();
    }

    @Override
    public void disableAlimMoteur() {
        log.info("Desactivation puissance moteur");
        pinAlimPuissanceMoteur.high();
    }

    @Override
    public void enableAlimServoMoteur() {
        log.info("Activation puissance servos-moteur");
        pinAlimPuissanceServosMoteur.low();
    }

    @Override
    public void disableAlimServoMoteur() {
        log.info("Desactivation puissance servos-moteur");
        pinAlimPuissanceServosMoteur.high();
    }
}
