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
import org.arig.robot.constants.IOdinConstantesI2C;
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

    private static final int POMPES_AVANT_GAUCHE = 2;
    private static final int POMPES_AVANT_DROIT = 3;
    private static final int POMPES_ARRIERE_GAUCHE = 1;
    private static final int POMPES_ARRIERE_DROIT = 4;

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
        outCmdLedCapteurRGB = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04, PinState.LOW);

        // Config PCF8574 //
        // -------------- //
        pcfAlim = new PCF8574GpioProvider(bus, IOdinConstantesI2C.PCF_ALIM_ADDRESS, true);
        pcf1 = new PCF8574GpioProvider(bus, IOdinConstantesI2C.PCF1_ADDRESS, true);

        // Alim
        inAu = gpio.provisionDigitalInputPin(pcfAlim, PCF8574Pin.GPIO_00);
        outAlimPuissance12V = gpio.provisionDigitalOutputPin(pcfAlim, PCF8574Pin.GPIO_01);
        outAlimPuissance5V = gpio.provisionDigitalOutputPin(pcfAlim, PCF8574Pin.GPIO_02);

        // PCF1
        inTirette = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_00);
        inCalageBordureGauche = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_02);
        inCalageBordureDroit = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_03);
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

        vacuumController.readAllValues();
    }

    // --------------------------------------------------------- //
    // --------------------- INFOS TECHNIQUE ------------------- //
    // --------------------------------------------------------- //

    @Override
    public boolean auOk() {
        return inAu.isLow();
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
        return vacuumController.getData(POMPES_AVANT_GAUCHE).presence();
    }

    @Override
    public boolean presenceVentouseAvantDroit() {
        return vacuumController.getData(POMPES_AVANT_DROIT).presence();
    }

    @Override
    public boolean presenceVentouseArriereGauche() {
        return vacuumController.getData(POMPES_ARRIERE_GAUCHE).presence();
    }

    @Override
    public boolean presenceVentouseArriereDroit() {
        return vacuumController.getData(POMPES_ARRIERE_DROIT).presence();
    }

    @Override
    public boolean presenceAvantGauche() {
        return vacuumController.getData(POMPES_AVANT_GAUCHE).tor();
    }

    @Override
    public boolean presenceAvantDroit() {
        return vacuumController.getData(POMPES_AVANT_DROIT).tor();
    }

    @Override
    public boolean presenceArriereGauche() {
        return vacuumController.getData(POMPES_ARRIERE_GAUCHE).tor();
    }

    @Override
    public boolean presenceArriereDroit() {
        return vacuumController.getData(POMPES_ARRIERE_DROIT).tor();
    }

    @Override
    public boolean calageBordureDroit() {
        return inCalageBordureDroit.isHigh();
    }

    @Override
    public boolean calageBordureGauche() {
        return inCalageBordureGauche.isHigh();
    }

    @Override
    public boolean calageBordureCustomDroit() {
        return presenceVentouseAvantDroit();
    }

    @Override
    public boolean calageBordureCustomGauche() {
        return presenceVentouseAvantGauche();
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
    public void disableAllPompe() {
        vacuumController.disableAll();
    }

    @Override
    public void enableAllPompe() {
        vacuumController.onAll();
    }

    @Override
    public void enablePompesAvant() {
        vacuumController.on(POMPES_AVANT_GAUCHE, POMPES_AVANT_DROIT);
    }

    @Override
    public void enablePompesArriere() {
        vacuumController.on(POMPES_ARRIERE_GAUCHE, POMPES_ARRIERE_DROIT);
    }

    @Override
    public void enablePompeAvantGauche() {
        vacuumController.on(POMPES_AVANT_GAUCHE);
    }

    @Override
    public void enablePompeAvantDroit() {
        vacuumController.on(POMPES_AVANT_DROIT);
    }

    @Override
    public void enablePompeArriereGauche() {
        vacuumController.on(POMPES_ARRIERE_GAUCHE);
    }

    @Override
    public void enablePompeArriereDroit() {
        vacuumController.on(POMPES_ARRIERE_DROIT);
    }

    @Override
    public void releaseAllPompe() {
        vacuumController.offAll();
    }

    @Override
    public void releasePompesAvant() {
        vacuumController.off(POMPES_AVANT_GAUCHE, POMPES_AVANT_DROIT);
    }

    @Override
    public void releasePompesArriere() {
        vacuumController.off(POMPES_ARRIERE_GAUCHE, POMPES_ARRIERE_DROIT);
    }

    @Override
    public void releasePompeAvantGauche() {
        vacuumController.off(POMPES_AVANT_GAUCHE);
    }

    @Override
    public void releasePompeAvantDroit() {
        vacuumController.off(POMPES_AVANT_DROIT);
    }

    @Override
    public void releasePompeArriereGauche() {
        vacuumController.off(POMPES_ARRIERE_GAUCHE);
    }

    @Override
    public void releasePompeArriereDroit() {
        vacuumController.off(POMPES_ARRIERE_DROIT);
    }
}
