package org.arig.robot.services;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.i2c.I2CBus;
import lombok.extern.slf4j.Slf4j;
import org.arig.pi4j.gpio.extension.pcf.PCF8574GpioProvider;
import org.arig.pi4j.gpio.extension.pcf.PCF8574Pin;
import org.arig.robot.constants.IEurobotConfig;
import org.arig.robot.constants.INerellConstantesI2C;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.system.capteurs.TCS34725ColorSensor;
import org.arig.robot.system.capteurs.TCS34725ColorSensor.ColorData;
import org.arig.robot.system.vacuum.AbstractARIGVacuumController;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service("IOService")
public class NerellIOService implements INerellIOService, InitializingBean, DisposableBean {

    private static final int POMPE_AVANT_1 = 1;
    private static final int POMPE_AVANT_2 = 2;
    private static final int POMPE_AVANT_3 = 3;
    private static final int POMPE_AVANT_4 = 4;

    @Autowired
    private I2CBus bus;

    @Autowired
    private AbstractARIGVacuumController vacuumController;

    @Autowired
    private TCS34725ColorSensor couleurAvant1;

    @Autowired
    private TCS34725ColorSensor couleurAvant2;

    @Autowired
    private TCS34725ColorSensor couleurAvant3;

    @Autowired
    private TCS34725ColorSensor couleurAvant4;

    @Autowired
    private TCS34725ColorSensor couleurArriere2;

    @Autowired
    private TCS34725ColorSensor couleurArriere4;

    // Controlleur GPIO
    private GpioController gpio;
    private PCF8574GpioProvider pcfAlim;
    private PCF8574GpioProvider pcf2;

    // Référence sur les PIN Inputs
    // ----------------------------

    // IRQ
//    private GpioPinDigitalInput inIrqAlim;
//    private GpioPinDigitalInput inIrqPcf1;
//    private GpioPinDigitalInput inIrq1;
//    private GpioPinDigitalInput inIrq3;
//    private GpioPinDigitalInput inIrq4;
//    private GpioPinDigitalInput inIrq5;
//    private GpioPinDigitalInput inIrq6;

    // Technique
    private GpioPinDigitalInput inAu;
    private GpioPinDigitalInput inAlimPuissance5V;
    private GpioPinDigitalInput inAlimPuissance12V;
    private GpioPinDigitalInput inTirette;

    // Input : Numerique
    private GpioPinDigitalInput inCalageBordureDroit;
    private GpioPinDigitalInput inCalageBordureGauche;
    private GpioPinDigitalInput inPresencePinceArriere1;
    private GpioPinDigitalInput inPresencePinceArriere2;
    private GpioPinDigitalInput inPresencePinceArriere3;
    private GpioPinDigitalInput inPresencePinceArriere4;
    private GpioPinDigitalInput inPresencePinceArriere5;

    // Référence sur les PIN Output
    // ----------------------------

    // GPIO
    private GpioPinDigitalOutput outCmdLedCapteurRGB;

    // PCF
    private GpioPinDigitalOutput outAlimPuissance5V;
    private GpioPinDigitalOutput outAlimPuissance12V;

    @Override
    public void destroy() throws Exception {
        try {
            if (pcfAlim != null) {
                pcfAlim.shutdown();
            }
        } catch (Exception e) {
            log.warn("Problème de shutdown du PCF ALim : {}", e.getMessage());
        }
        try {
            if (pcf2 != null) {
                pcf2.shutdown();
            }
        } catch (Exception e) {
            log.warn("Problème de shutdown du PCF 2 : {}", e.getMessage());
        }
        try {
            if (gpio != null) {
                gpio.shutdown();
            }
        } catch (Exception e) {
            log.warn("Problème de shutdown du GPIO Raspi : {}", e.getMessage());
        }
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

        // Output
        outCmdLedCapteurRGB = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04, PinState.LOW);

        // Config PCF8574 //
        // -------------- //
        pcfAlim = new PCF8574GpioProvider(bus, INerellConstantesI2C.PCF_ALIM_ADDRESS, true);
        pcf2 = new PCF8574GpioProvider(bus, INerellConstantesI2C.PCF2_ADDRESS, true);

        // Alim
        inAu = gpio.provisionDigitalInputPin(pcfAlim, PCF8574Pin.GPIO_04);
        inAlimPuissance5V = gpio.provisionDigitalInputPin(pcfAlim, PCF8574Pin.GPIO_05);
        inAlimPuissance12V = gpio.provisionDigitalInputPin(pcfAlim, PCF8574Pin.GPIO_06);

        outAlimPuissance5V = gpio.provisionDigitalOutputPin(pcfAlim, PCF8574Pin.GPIO_00);
        outAlimPuissance12V = gpio.provisionDigitalOutputPin(pcfAlim, PCF8574Pin.GPIO_01);

        // PCF2
        inTirette = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_02);
        inPresencePinceArriere1 = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_05);
        inPresencePinceArriere2 = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_07);
        inPresencePinceArriere3 = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_04);
        inPresencePinceArriere4 = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_06);
        inPresencePinceArriere5 = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_03);
        inCalageBordureGauche = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_01);
        inCalageBordureDroit = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_00);

        disableLedCapteurCouleur();
    }

    @Override
    public void refreshAllIO() {
        try {
            if (!pcf2.isShutdown()) {
                pcf2.readAll();
            }
        } catch (IOException e) {
            log.error("Erreur lecture PCF 2 : " + e.getMessage(), e);
        }

        try {
            if (!pcfAlim.isShutdown()) {
                pcfAlim.readAll();
            }
        } catch (IOException e) {
            log.error("Erreur lecture PCF Alim : " + e.getMessage(), e);
        }

        vacuumController.readAllValues();
    }

    // --------------------------------------------------------- //
    // --------------------- INFOS TECHNIQUE ------------------- //
    // --------------------------------------------------------- //

    @Override
    public boolean auOk() {
        return inAu.isLow();
    }

    public boolean alimPuissance5VOk() {
        return inAlimPuissance5V.isHigh();
    }

    public boolean alimPuissance12VOk() {
        return inAlimPuissance12V.isHigh();
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
    public boolean presenceVentouse1() {
        return vacuumController.getData(POMPE_AVANT_1).presence();
    }

    @Override
    public boolean presenceVentouse2() {
        return vacuumController.getData(POMPE_AVANT_2).presence();
    }

    @Override
    public boolean presenceVentouse3() {
        return vacuumController.getData(POMPE_AVANT_3).presence();
    }

    @Override
    public boolean presenceVentouse4() {
        return vacuumController.getData(POMPE_AVANT_4).presence();
    }

    @Override
    public boolean presence1() {
        return vacuumController.getData(POMPE_AVANT_1).tor();
    }

    @Override
    public boolean presence2() {
        return vacuumController.getData(POMPE_AVANT_2).tor();
    }

    @Override
    public boolean presence3() {
        return vacuumController.getData(POMPE_AVANT_3).tor();
    }

    @Override
    public boolean presence4() {
        return vacuumController.getData(POMPE_AVANT_4).tor();
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
    public boolean calageBordureDroit() {
        return inCalageBordureDroit.isLow();
    }

    @Override
    public boolean calageBordureGauche() {
        return inCalageBordureGauche.isLow();
    }

    @Override
    public boolean calageBordureCustomDroit() {
        return false;
    }

    @Override
    public boolean calageBordureCustomGauche() {
        return false;
    }

    // Couleur
    private ECouleurBouee computeCouleurBouee(TCS34725ColorSensor capteur) {
        int deltaRouge = IEurobotConfig.deltaCapteurCouleurRouge;
        int deltaVert = IEurobotConfig.deltaCapteurCouleurVert;
        final ColorData c = capteur.getColorData();
        final ECouleurBouee result;
        if (c.g() > c.r() + deltaVert && c.g() > c.b() + deltaVert) {
            result = ECouleurBouee.VERT;
        } else if (c.r() > c.b() + deltaRouge && c.r() > c.g() + deltaRouge) {
            result = ECouleurBouee.ROUGE;
        } else {
            result = ECouleurBouee.INCONNU;
        }
        log.info("{} R: {}, G: {}, B: {}, Bouée: {}", capteur.deviceName(), c.r(), c.g(), c.b(), result.name());
        return result;
    }

    @Override
    public ECouleurBouee couleurBoueeAvant1() {
        return computeCouleurBouee(couleurAvant1);
    }

    @Override
    public ECouleurBouee couleurBoueeAvant2() {
        return computeCouleurBouee(couleurAvant2);
    }

    @Override
    public ECouleurBouee couleurBoueeAvant3() {
        return computeCouleurBouee(couleurAvant3);
    }

    @Override
    public ECouleurBouee couleurBoueeAvant4() {
        return computeCouleurBouee(couleurAvant4);
    }

    @Override
    public ECouleurBouee couleurBoueeArriere2() {
        return computeCouleurBouee(couleurArriere2);
    }

    @Override
    public ECouleurBouee couleurBoueeArriere4() {
        return computeCouleurBouee(couleurArriere4);
    }

    // --------------------------------------------------------- //
    // -------------------------- OUTPUT ----------------------- //
    // --------------------------------------------------------- //

    @Override
    public void enableLedCapteurCouleur() {
        log.debug("Led blanche capteur couleur allumé");
        outCmdLedCapteurRGB.high();
    }

    @Override
    public void disableLedCapteurCouleur() {
        log.debug("Led blanche capteur couleur eteinte");
        outCmdLedCapteurRGB.low();
    }

    @Override
    public void enableAlimServos() {
        log.info("Activation puissance 5V");
        outAlimPuissance5V.low();
    }

    @Override
    public void disableAlimServos() {
        log.info("Desactivation puissance 5V");
        outAlimPuissance5V.high();
    }

    @Override
    public void enableAlimMoteurs() {
        log.info("Activation puissance 12V");
        outAlimPuissance12V.low();
    }

    @Override
    public void disableAlimMoteurs() {
        log.info("Desactivation puissance 12V");
        outAlimPuissance12V.high();
    }

    // ----------------------------------------------------------- //
    // -------------------------- BUSINESS ----------------------- //
    // ----------------------------------------------------------- //


    @Override
    public void disableAllPompes() {
        vacuumController.disableAll();
    }

    @Override
    public void enableAllPompes() {
        vacuumController.onAll();
    }

    @Override
    public void enablePompe1() {
        vacuumController.on(POMPE_AVANT_1);
    }

    @Override
    public void enablePompe2() {
        vacuumController.on(POMPE_AVANT_2);
    }

    @Override
    public void enablePompe3() {
        vacuumController.on(POMPE_AVANT_3);
    }

    @Override
    public void enablePompe4() {
        vacuumController.on(POMPE_AVANT_4);
    }

    @Override
    public void releaseAllPompes() {
        vacuumController.offAll();
    }

    @Override
    public void releasePompe1() {
        vacuumController.off(POMPE_AVANT_1);
    }

    @Override
    public void releasePompe2() {
        vacuumController.off(POMPE_AVANT_2);
    }

    @Override
    public void releasePompe3() {
        vacuumController.off(POMPE_AVANT_3);
    }

    @Override
    public void releasePompe4() {
        vacuumController.off(POMPE_AVANT_4);
    }
}
