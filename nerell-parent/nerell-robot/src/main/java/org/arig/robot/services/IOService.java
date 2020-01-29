package org.arig.robot.services;

import com.pi4j.io.gpio.*;
import com.pi4j.io.i2c.I2CBus;
import lombok.extern.slf4j.Slf4j;
import org.arig.pi4j.gpio.extension.pcf.PCF8574GpioProvider;
import org.arig.pi4j.gpio.extension.pcf.PCF8574Pin;
import org.arig.robot.constants.IConstantesI2C;
import org.arig.robot.constants.IConstantesUtiles;
import org.arig.robot.model.EStrategy;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.Team;
import org.arig.robot.system.capteurs.I2CAdcAnalogInput;
import org.arig.robot.system.capteurs.TCS34725ColorSensor;
import org.arig.robot.system.capteurs.TCS34725ColorSensor.ColorData;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class IOService implements IIOService, InitializingBean, DisposableBean {

    @Autowired
    private RobotStatus rs;

    @Autowired
    private I2CBus bus;

    @Autowired
    private I2CAdcAnalogInput i2cAdc;

    @Autowired
    private TCS34725ColorSensor colorSensor;

    // Controlleur GPIO
    private GpioController gpio;
    private PCF8574GpioProvider pcfAlim;
    private PCF8574GpioProvider pcf1;
    private PCF8574GpioProvider pcf2;
    private PCF8574GpioProvider pcf3;

    // Référence sur les PIN Inputs
    // ----------------------------

    // IRQ
    private GpioPinDigitalInput inIrqAlim;
    private GpioPinDigitalInput inIrqPcf1;
    private GpioPinDigitalInput inIrq1;
    private GpioPinDigitalInput inIrq3;
    private GpioPinDigitalInput inIrq4;
    private GpioPinDigitalInput inIrq5;
    private GpioPinDigitalInput inIrq6;

    // Technique
    private GpioPinDigitalInput inAu;
    private GpioPinDigitalInput inAlimPuissance5V;
    private GpioPinDigitalInput inAlimPuissance12V;
    private GpioPinDigitalInput inTirette;

    // Input : Numerique
    private GpioPinDigitalInput inPresenceLectureCouleur;
    private GpioPinDigitalInput inCalageBordureDroit;
    private GpioPinDigitalInput inCalageBordureGauche;
    private GpioPinDigitalInput inPresencePinceAvant1;
    private GpioPinDigitalInput inPresencePinceAvant2;
    private GpioPinDigitalInput inPresencePinceAvant3;
    private GpioPinDigitalInput inPresencePinceAvant4;
    private GpioPinDigitalInput inPresencePinceArriere1;
    private GpioPinDigitalInput inPresencePinceArriere2;
    private GpioPinDigitalInput inPresencePinceArriere3;
    private GpioPinDigitalInput inPresencePinceArriere4;
    private GpioPinDigitalInput inPresencePinceArriere5;

    // Référence sur les PIN Output
    // ----------------------------

    // GPIO
    private GpioPinDigitalOutput outCmdLedCapteurRGB;
    private GpioPinDigitalOutput outCmdMoteurDrapeau;

    // PCF
    private GpioPinDigitalOutput outAlimPuissance5V;
    private GpioPinDigitalOutput outAlimPuissance12V;

    @Override
    public void destroy() throws Exception {
        pcfAlim.shutdown();
        pcf1.shutdown();
        pcf2.shutdown();
        pcf3.shutdown();
        gpio.shutdown();
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        // Config des IO raspi //
        // ------------------- //
        gpio = GpioFactory.getInstance();

        // Inputs
//        inEquipe = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02);
//        inIrqAlim = gpio.provisionDigitalInputPin(RaspiPin.GPIO_07);
//        inIrqPcf1 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_04);
//        inIrq1 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_00);
//        inIrq3 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_01);
//        inIrq4 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_16);
//        inIrq5 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_15);
//        inIrq6 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_06);

        // Config PCF8574 //
        // -------------- //
        // TODO Config IRQ
        pcfAlim = new PCF8574GpioProvider(bus, IConstantesI2C.PCF_ALIM_ADDRESS, true);
        pcf1 = new PCF8574GpioProvider(bus, IConstantesI2C.PCF1_ADDRESS, true);
        pcf2 = new PCF8574GpioProvider(bus, IConstantesI2C.PCF2_ADDRESS, true);
        pcf3 = new PCF8574GpioProvider(bus, IConstantesI2C.PCF3_ADDRESS, true);

        // Alim
        inAu = gpio.provisionDigitalInputPin(pcfAlim, PCF8574Pin.GPIO_04);
        inAlimPuissance5V = gpio.provisionDigitalInputPin(pcfAlim, PCF8574Pin.GPIO_05);
        inAlimPuissance12V = gpio.provisionDigitalInputPin(pcfAlim, PCF8574Pin.GPIO_06);

        outAlimPuissance5V = gpio.provisionDigitalOutputPin(pcfAlim, PCF8574Pin.GPIO_00);
        outAlimPuissance12V = gpio.provisionDigitalOutputPin(pcfAlim, PCF8574Pin.GPIO_01);

        // PCF1
        inTirette = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_00);
        inPresenceLectureCouleur = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_01);
        inPresencePinceAvant1 = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_02);
        inPresencePinceAvant2 = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_03);
        inPresencePinceAvant3 = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_04);
        inPresencePinceAvant4 = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_05);

        // PCF2
        inCalageBordureDroit = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_00);
        inCalageBordureGauche = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_01);
        inPresencePinceArriere1 = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_02);
        inPresencePinceArriere2 = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_03);
        inPresencePinceArriere3 = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_04);
        inPresencePinceArriere4 = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_05);
        inPresencePinceArriere5 = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_06);

        // PCF3
        outCmdLedCapteurRGB = gpio.provisionDigitalOutputPin(pcf3, PCF8574Pin.GPIO_00, PinState.LOW);
        outCmdMoteurDrapeau = gpio.provisionDigitalOutputPin(pcf3, PCF8574Pin.GPIO_01, PinState.LOW);

        // Etat initial des IOs
        disableLedCapteurCouleur();
    }

    @Override
    public void refreshAllPcf() {
        try {
            if (!pcf1.isShutdown()) {
                pcf1.readAll();
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        try {
            if (!pcf2.isShutdown()) {
                pcf2.readAll();
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        try {
            if (!pcfAlim.isShutdown()) {
                pcfAlim.readAll();
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    // --------------------------------------------------------- //
    // --------------------- INFOS TECHNIQUE ------------------- //
    // --------------------------------------------------------- //

    @Override
    public boolean auOk() {
        boolean result = inAu.isLow();
        log.info("AU present : {}", result);
        return result;
    }

    @Override
    public boolean alimPuissance5VOk() {
        boolean result = inAlimPuissance5V.isHigh();
        log.info("Puissance 5V present : {}", result);
        return result;
    }

    @Override
    public boolean alimPuissance12VOk() {
        boolean result = inAlimPuissance12V.isHigh();
        log.info("Puisance 12V present : {}", result);
        return result;
    }

    @Override
    public boolean tirette() {
        return inTirette.isLow();
    }

    // --------------------------------------------------------- //
    // -------------------------- INPUT ------------------------ //
    // --------------------------------------------------------- //

    // Numerique

    @Override
    public boolean presenceLectureCouleur() {
        return inPresenceLectureCouleur.isLow();
    }

    @Override
    public boolean presencePinceAvant1() {
        return inPresencePinceAvant1.isLow();
    }

    @Override
    public boolean presencePinceAvant2() {
        return inPresencePinceAvant2.isLow();
    }

    @Override
    public boolean presencePinceAvant3() {
        return inPresencePinceAvant3.isLow();
    }

    @Override
    public boolean presencePinceAvant4() {
        return inPresencePinceAvant4.isLow();
    }

    @Override
    public boolean presencePinceArriere1() {
        return inPresencePinceArriere1.isLow();
    }

    @Override
    public boolean presencePinceArriere2() {
        return inPresencePinceArriere2.isLow();
    }

    @Override
    public boolean presencePinceArriere3() {
        return inPresencePinceArriere3.isLow();
    }

    @Override
    public boolean presencePinceArriere4() {
        return inPresencePinceArriere4.isLow();
    }

    @Override
    public boolean presencePinceArriere5() {
        return inPresencePinceArriere5.isLow();
    }

    @Override
    public boolean calageBordureArriereDroit() {
        return inCalageBordureDroit.isLow();
    }

    @Override
    public boolean calageBordureArriereGauche() {
        return inCalageBordureGauche.isLow();
    }

    // Couleur
    @Override
    public ColorData couleurRaw() {
        return colorSensor.getColorData();
    }

    // --------------------------------------------------------- //
    // -------------------------- OUTPUT ----------------------- //
    // --------------------------------------------------------- //

    @Override
    public void enableLedCapteurCouleur() {
        log.info("Led blanche capteur couleur allumé");
        outCmdLedCapteurRGB.high();
    }

    @Override
    public void disableLedCapteurCouleur() {
        log.info("Led blanche capteur couleur eteinte");
        outCmdLedCapteurRGB.low();
    }

    @Override
    public void enableMoteurDrapeau() {
        log.info("Activation drapeau");
        outCmdMoteurDrapeau.high();
    }

    @Override
    public void disableMoteurDrapeau() {
        log.info("Désactivation drapeau");
        outCmdMoteurDrapeau.low();
    }

    @Override
    public void enableAlim5VPuissance() {
        log.info("Activation puissance 5V");
        outAlimPuissance5V.low();
    }

    @Override
    public void disableAlim5VPuissance() {
        log.info("Desactivation puissance 5V");
        outAlimPuissance5V.high();
    }

    @Override
    public void enableAlim12VPuissance() {
        log.info("Activation puissance 12V");
        outAlimPuissance12V.low();
    }

    @Override
    public void disableAlim12VPuissance() {
        log.info("Desactivation puissance 12V");
        outAlimPuissance12V.high();
    }

}
