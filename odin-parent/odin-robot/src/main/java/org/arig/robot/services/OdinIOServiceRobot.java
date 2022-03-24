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
import org.arig.robot.constants.OdinConstantesI2C;
import org.arig.robot.model.Couleur;
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
public class OdinIOServiceRobot implements OdinIOService, InitializingBean, DisposableBean {

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
    private GpioPinDigitalOutput outAlimPuissanceServos;
    private GpioPinDigitalOutput outAlimPuissanceMoteurs;

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
        pcfAlim = new PCF8574GpioProvider(bus, OdinConstantesI2C.PCF_ALIM_ADDRESS, true);
        pcf1 = new PCF8574GpioProvider(bus, OdinConstantesI2C.PCF1_ADDRESS, true);

        // Alim
        inAu = gpio.provisionDigitalInputPin(pcfAlim, PCF8574Pin.GPIO_00);
        outAlimPuissanceMoteurs = gpio.provisionDigitalOutputPin(pcfAlim, PCF8574Pin.GPIO_01);
        outAlimPuissanceServos = gpio.provisionDigitalOutputPin(pcfAlim, PCF8574Pin.GPIO_02);

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
    public boolean calageBordureArriereDroit() {
        return inCalageBordureDroit.isHigh();
    }

    @Override
    public boolean calageBordureArriereGauche() {
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
    private Couleur computeCouleurBouee(TCS34725ColorSensor capteur) {
        final ColorData c = capteur.getColorData();
        log.info("{} R: {}, G: {}, B: {}", capteur.deviceName(), c.r(), c.g(), c.b());
        return Couleur.INCONNU;
    }

    @Override
    public Couleur couleurAvantGauche() {
        return computeCouleurBouee(couleurAvantGauche);
    }

    @Override
    public Couleur couleurAvantDroit() {
        return computeCouleurBouee(couleurAvantDroit);
    }

    @Override
    public Couleur couleurArriereGauche() {
        return computeCouleurBouee(couleurArriereGauche);
    }

    @Override
    public Couleur couleurArriereDroit() {
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
        log.info("Activation puissance servos");
        outAlimPuissanceServos.low();
    }

    @Override
    public void disableAlimServos() {
        log.info("Desactivation puissance servos");
        outAlimPuissanceServos.high();
    }

    @Override
    public void enableAlimMoteurs() {
        log.info("Activation puissance moteurs 12V");
        outAlimPuissanceMoteurs.low();
    }

    @Override
    public void disableAlimMoteurs() {
        log.info("Desactivation puissance moteurs 12V");
        outAlimPuissanceMoteurs.high();
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
