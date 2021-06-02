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
import org.arig.robot.constants.IConstantesI2COdin;
import org.arig.robot.constants.IEurobotConfig;
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
public class OdinIOService implements IOdinIOService, InitializingBean, DisposableBean {

    @Autowired
    private I2CBus bus;

    @Autowired
    private AbstractARIGVacuumController vacuumController;

    @Autowired
    private TCS34725ColorSensor couleurAvantGauche;

    @Autowired
    private TCS34725ColorSensor couleurAvantDroit;

    @Autowired
    private TCS34725ColorSensor couleurArriereGauche;

    @Autowired
    private TCS34725ColorSensor couleurArriereDroit;

    // Controlleur GPIO
    private GpioController gpio;
    private PCF8574GpioProvider pcfAlim;
    private PCF8574GpioProvider pcf1;

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
            if (pcf1 != null) {
                pcf1.shutdown();
            }
        } catch (Exception e) {
            log.warn("Problème de shutdown du PCF 1 : {}", e.getMessage());
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
        outCmdLedCapteurRGB = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_05, PinState.LOW); // TODO

        // Config PCF8574 //
        // -------------- //
        pcfAlim = new PCF8574GpioProvider(bus, IConstantesI2COdin.PCF_ALIM_ADDRESS, true);
        pcf1 = new PCF8574GpioProvider(bus, IConstantesI2COdin.PCF1_ADDRESS, true);

        // Alim
        inAu = gpio.provisionDigitalInputPin(pcfAlim, PCF8574Pin.GPIO_04);
        inAlimPuissance5V = gpio.provisionDigitalInputPin(pcfAlim, PCF8574Pin.GPIO_05);
        inAlimPuissance12V = gpio.provisionDigitalInputPin(pcfAlim, PCF8574Pin.GPIO_06);

        outAlimPuissance5V = gpio.provisionDigitalOutputPin(pcfAlim, PCF8574Pin.GPIO_00);
        outAlimPuissance12V = gpio.provisionDigitalOutputPin(pcfAlim, PCF8574Pin.GPIO_01);

        // PCF1
        inTirette = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_02);
        inCalageBordureGauche = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_01);
        inCalageBordureDroit = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_00);
    }

    @Override
    public void refreshAllIO() {
        try {
            if (!pcf1.isShutdown()) {
                pcf1.readAll();
            }
        } catch (IOException e) {
            log.error("Erreur lecture PCF 1 : " + e.getMessage(), e);
        }

        try {
            if (!pcfAlim.isShutdown()) {
                pcfAlim.readAll();
            }
        } catch (IOException e) {
            log.error("Erreur lecture PCF Alim : " + e.getMessage(), e);
        }
    }

    // --------------------------------------------------------- //
    // --------------------- INFOS TECHNIQUE ------------------- //
    // --------------------------------------------------------- //

    @Override
    public boolean auOk() {
        return inAu.isLow();
    }

    @Override
    public boolean alimPuissance5VOk() {
        return inAlimPuissance5V.isHigh();
    }

    @Override
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
    public boolean presenceVentouseAvantGauche() {
        return vacuumController.getData(1).presence(); // TODO
    }

    @Override
    public boolean presenceVentouseAvantDroit() {
        return vacuumController.getData(2).presence(); // TODO
    }

    @Override
    public boolean presenceVentouseArriereGauche() {
        return vacuumController.getData(3).presence(); // TODO
    }

    @Override
    public boolean presenceVentouseArriereDroit() {
        return vacuumController.getData(4).presence(); // TODO
    }

    @Override
    public boolean calageBordureDroit() {
        return inCalageBordureDroit.isLow();
    }

    @Override
    public boolean calageBordureGauche() {
        return inCalageBordureGauche.isLow();
    }

    // Couleur
    private ECouleurBouee computeCouleurBouee(TCS34725ColorSensor capteur) {
        int delta = IEurobotConfig.deltaCapteurCouleur;
        final ColorData c = capteur.getColorData();
        final ECouleurBouee result;
        if (c.g() > c.r() + delta && c.g() > c.b() + delta) {
            result = ECouleurBouee.VERT;
        } else if (c.r() > c.b() + delta && c.r() > c.g() + delta) {
            result = ECouleurBouee.ROUGE;
        } else {
            result = ECouleurBouee.INCONNU;
        }
        log.info("{} R: {}, G: {}, B: {}, Bouée: {}", capteur.deviceName(), c.r(), c.g(), c.b(), result.name());
        return result;
    }

    @Override
    public ECouleurBouee couleurBoueeAvantGauche() {
        return computeCouleurBouee(couleurAvantGauche);
    }

    @Override
    public ECouleurBouee couleurBoueeAvantDroit() {
        return computeCouleurBouee(couleurAvantDroit);
    }

    @Override
    public ECouleurBouee couleurBoueeArriereGauche() {
        return computeCouleurBouee(couleurArriereGauche);
    }

    @Override
    public ECouleurBouee couleurBoueeArriereDroit() {
        return computeCouleurBouee(couleurArriereDroit);
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

    // ----------------------------------------------------------- //
    // -------------------------- BUSINESS ----------------------- //
    // ----------------------------------------------------------- //

    @Override
    public void disableAllPompe() {
        vacuumController.disableAll();
    }

    @Override
    public void enableAllPompe() {
        vacuumController.onAll();
    }

    @Override
    public void enablePompeAvantGauche() {
        vacuumController.on(1); // TODO
    }

    @Override
    public void enablePompeAvantDroit() {
        vacuumController.on(2); // TODO
    }

    @Override
    public void enablePompeArriereGauche() {
        vacuumController.on(3); // TODO
    }

    @Override
    public void enablePompeArriereDroit() {
        vacuumController.on(4); // TODO
    }

    @Override
    public void releaseAllPompe() {
        vacuumController.offAll();
    }

    @Override
    public void releasePompeAvantGauche() {
        vacuumController.off(1); // TODO
    }

    @Override
    public void releasePompeAvantDroit() {
        vacuumController.off(2); // TODO
    }

    @Override
    public void releasePompeArriereGauche() {
        vacuumController.off(3); // TODO
    }

    @Override
    public void releasePompeArriereDroit() {
        vacuumController.off(4); // TODO
    }
}
