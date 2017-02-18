package org.arig.robot.services;

import com.pi4j.gpio.extension.pcf.PCF8574GpioProvider;
import com.pi4j.io.gpio.*;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesIORaspi;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.Team;
import org.arig.robot.system.capteurs.TCS34725ColorSensor;
import org.arig.robot.system.capteurs.TCS34725ColorSensor.ColorData;
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
    @Qualifier("ioRaspi")
    private GpioController gpio;

    @Autowired
    @Qualifier("pcfAlim")
    private PCF8574GpioProvider pcfAlim;

    @Autowired
    @Qualifier("frontColorSensor")
    private TCS34725ColorSensor frontColorSensor;

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
        gpio.provisionDigitalInputPin(IConstantesIORaspi.IRQ_ALIM);

        // Output
        pinCmdLedCapteurRGB = gpio.provisionDigitalOutputPin(IConstantesIORaspi.CMD_LED_CAPTEUR_RGB, PinState.LOW);
        pinLedRGB_R = gpio.provisionDigitalOutputPin(IConstantesIORaspi.PWM_R);
        pinLedRGB_G = gpio.provisionDigitalOutputPin(IConstantesIORaspi.PWM_G);
        pinLedRGB_B = gpio.provisionDigitalOutputPin(IConstantesIORaspi.PWM_B);

        // On éteint la led du capteur, elle pique les yeux.
        pinCmdLedCapteurRGB.low();
        clearTeamColor();
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
    public ColorData frontColor() {
        return frontColorSensor.getColorData();
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

    // --------------------------------------------------------- //
    // ---------------- NON IMPLEMENTE DANS UTILS -------------- //
    // --------------------------------------------------------- //

    @Override
    public boolean btnTapis() {
        return false;
    }

    @Override
    public Team equipe() {
        return null;
    }

    @Override
    public boolean tirette() {
        return false;
    }

    @Override
    public boolean buteeAvantGauche() {
        return false;
    }

    @Override
    public boolean buteeAvantDroit() {
        return false;
    }

    @Override
    public boolean buteeArriereGauche() {
        return false;
    }

    @Override
    public boolean buteeArriereDroit() {
        return false;
    }

    @Override
    public boolean produitGauche() {
        return false;
    }

    @Override
    public boolean gobeletGauche() {
        return false;
    }

    @Override
    public boolean piedGauche() {
        return false;
    }

    @Override
    public boolean produitDroit() {
        return false;
    }

    @Override
    public boolean gobeletDroit() {
        return false;
    }

    @Override
    public boolean piedDroit() {
        return false;
    }

    @Override
    public boolean piedCentre() {
        return false;
    }
}
