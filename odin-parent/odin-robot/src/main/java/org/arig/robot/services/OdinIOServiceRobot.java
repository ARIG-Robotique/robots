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
import org.arig.robot.model.CouleurEchantillon;
import org.arig.robot.system.capteurs.TCS34725ColorSensor;
import org.arig.robot.system.vacuum.AbstractARIGVacuumController;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service("IOService")
public class OdinIOServiceRobot implements OdinIOService, InitializingBean, DisposableBean {

    private static final int POMPE_VENTOUSE_BAS = 3;
    private static final int POMPE_VENTOUSE_HAUT = 4;

    @Autowired
    private I2CBus bus;

    @Autowired
    private AbstractARIGVacuumController vacuumController;

    @Autowired
    private TCS34725ColorSensor couleurVentouseBas;

    @Autowired
    private TCS34725ColorSensor couleurVentouseHaut;

    // Controlleur GPIO
    private GpioController gpio;
    private PCF8574GpioProvider pcfAlim;
    private PCF8574GpioProvider pcf1;
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

    // Input : Alimentation
    private GpioPinDigitalInput inAu;
    private GpioPinDigitalInput inAlimPuissanceServos;
    private GpioPinDigitalInput inAlimPuissanceMoteurs;

    // Input : Numerique 1 (+ Tirette)
    private GpioPinDigitalInput inTirette;
    private GpioPinDigitalInput inCalageArriereDroit;
    private GpioPinDigitalInput inCalageArriereGauche;
    private GpioPinDigitalInput inCalageAvantBasDroit;
    private GpioPinDigitalInput inCalageAvantBasGauche;
    private GpioPinDigitalInput inCalageAvantHautDroit;
    private GpioPinDigitalInput inCalageAvantHautGauche;
    private GpioPinDigitalInput inPresenceStatuette;

    // Input : Numerique 2
    private GpioPinDigitalInput inPresenceCarreFouille;
    private GpioPinDigitalInput inPresencePriseBras;
    private GpioPinDigitalInput inPresenceStock1; // Fond du robot
    private GpioPinDigitalInput inPresenceStock2;
    private GpioPinDigitalInput inPresenceStock3;
    private GpioPinDigitalInput inPresenceStock4;
    private GpioPinDigitalInput inPresenceStock5;
    private GpioPinDigitalInput inPresenceStock6; // Bord du robot

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
            log.warn("Problème de shutdown du {} : {}", OdinConstantesI2C.PCF1_DEVICE_NAME, e.getMessage());
        }
        try {
            if (pcf2 != null) {
                pcf2.shutdown();
            }
        } catch (Exception e) {
            log.warn("Problème de shutdown du {} : {}", OdinConstantesI2C.PCF2_DEVICE_NAME, e.getMessage());
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
        pcf2 = new PCF8574GpioProvider(bus, OdinConstantesI2C.PCF2_ADDRESS, true);

        // Alim
        inAu = gpio.provisionDigitalInputPin(pcfAlim, PCF8574Pin.GPIO_04);
        inAlimPuissanceServos = gpio.provisionDigitalInputPin(pcfAlim, PCF8574Pin.GPIO_05);
        inAlimPuissanceMoteurs = gpio.provisionDigitalInputPin(pcfAlim, PCF8574Pin.GPIO_06);

        outAlimPuissanceServos = gpio.provisionDigitalOutputPin(pcfAlim, PCF8574Pin.GPIO_00);
        outAlimPuissanceMoteurs = gpio.provisionDigitalOutputPin(pcfAlim, PCF8574Pin.GPIO_01);

        // PCF1 (µSwitch)
        inTirette = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_04);
        inCalageArriereDroit = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_05);
        inCalageArriereGauche = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_03);
        inCalageAvantBasDroit = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_07);
        inCalageAvantBasGauche = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_06);
        inCalageAvantHautDroit = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_00);
        inCalageAvantHautGauche = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_01);
        inPresenceStatuette = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_02);

        // PCF2 (Pololu)
        inPresenceCarreFouille = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_00);
        inPresencePriseBras = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_01);
        inPresenceStock1 = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_06);
        inPresenceStock2 = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_05);
        inPresenceStock3 = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_04);
        inPresenceStock4 = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_03);
        inPresenceStock5 = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_02);
        inPresenceStock6 = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_07);
    }

    @Override
    public void refreshAllIO() {
        try {
            if (!pcf1.isShutdown()) {
                pcf1.readAll();
            }
        } catch (IOException e) {
            log.error("Erreur lecture " + OdinConstantesI2C.PCF1_DEVICE_NAME + " : " + e.getMessage(), e);
        }

        try {
            if (!pcf2.isShutdown()) {
                pcf2.readAll();
            }
        } catch (IOException e) {
            log.error("Erreur lecture " + OdinConstantesI2C.PCF2_DEVICE_NAME + " : " + e.getMessage(), e);
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

    public boolean puissanceServosOk() {
        return inAlimPuissanceServos.isHigh();
    }

    public boolean puissanceMoteursOk() {
        return inAlimPuissanceMoteurs.isHigh();
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
    public boolean presenceStatuette(boolean expectedSimulation) {
        return inPresenceStatuette.isLow();
    }

    @Override
    public boolean presenceCarreFouille(final boolean expectedSimulation) {
        return inPresenceCarreFouille.isLow();
    }

    @Override
    public boolean presenceVentouseBas() {
        return vacuumController.getData(POMPE_VENTOUSE_BAS).presence();
    }

    @Override
    public boolean presenceVentouseHaut() {
        return vacuumController.getData(POMPE_VENTOUSE_HAUT).presence();
    }

    @Override
    public boolean presencePriseBras(boolean expectedSimulation) {
        return inPresencePriseBras.isLow();
    }

    @Override
    public boolean presenceStock1(boolean expectedSimulation) {
        return inPresenceStock1.isLow();
    }

    @Override
    public boolean presenceStock2(boolean expectedSimulation) {
        return inPresenceStock2.isLow();
    }

    @Override
    public boolean presenceStock3(boolean expectedSimulation) {
        return inPresenceStock3.isLow();
    }

    @Override
    public boolean presenceStock4(boolean expectedSimulation) {
        return inPresenceStock4.isLow();
    }

    @Override
    public boolean presenceStock5(boolean expectedSimulation) {
        return inPresenceStock5.isLow();
    }

    @Override
    public boolean presenceStock6(boolean expectedSimulation) {
        return inPresenceStock6.isLow();
    }

    @Override
    public boolean calageArriereDroit() {
        return inCalageArriereDroit.isLow();
    }

    @Override
    public boolean calageArriereGauche() {
        return inCalageArriereGauche.isLow();
    }

    @Override
    public boolean calageAvantBasDroit() {
        return inCalageAvantBasDroit.isLow();
    }

    @Override
    public boolean calageAvantBasGauche() {
        return inCalageAvantBasGauche.isLow();
    }

    @Override
    public boolean calageAvantHautDroit() {
        return inCalageAvantHautDroit.isLow();
    }

    @Override
    public boolean calageAvantHautGauche() {
        return inCalageAvantHautGauche.isLow();
    }

    @Override
    public boolean calageLatteralDroit() {
        return !presenceCarreFouille(false);
    }

    @Override
    public boolean calagePriseEchantillon() {
        return presencePriseBras(true);
    }

    @Override
    public boolean calageVentouseBas() {
        return presenceVentouseBas();
    }

    // Couleur
    @Override
    public CouleurEchantillon couleurVentouseBas() {
        final TCS34725ColorSensor.ColorData c = couleurVentouseBas.getColorData();
        log.info("{} R: {}, G: {}, B: {}", couleurVentouseBas.deviceName(), c.r(), c.g(), c.b());
        return computeCouleur(c);
    }

    @Override
    public CouleurEchantillon couleurVentouseHaut() {
        final TCS34725ColorSensor.ColorData c = couleurVentouseHaut.getColorData();
        log.info("{} R: {}, G: {}, B: {}", couleurVentouseHaut.deviceName(), c.r(), c.g(), c.b());
        return computeCouleur(c);
    }

    @Override
    public TCS34725ColorSensor.ColorData couleurVentouseHautRaw() {
        return couleurVentouseHaut.getColorData();
    }

    @Override
    public TCS34725ColorSensor.ColorData couleurVentouseBasRaw() {
        return couleurVentouseBas.getColorData();
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
        log.info("Activation puissance moteurs");
        outAlimPuissanceMoteurs.low();
    }

    @Override
    public void disableAlimMoteurs() {
        log.info("Desactivation puissance moteurs");
        outAlimPuissanceMoteurs.high();
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
    public void enableForceAllPompes() {
        vacuumController.forceOnAll();
    }

    @Override
    public void releaseAllPompes() {
        vacuumController.offAll();
    }

    @Override
    public void enableForcePompeVentouseBas() {
        vacuumController.onForce(POMPE_VENTOUSE_BAS);
    }

    @Override
    public void enableForcePompeVentouseHaut() {
        vacuumController.onForce(POMPE_VENTOUSE_HAUT);
    }

    @Override
    public void enablePompeVentouseBas() {
        vacuumController.on(POMPE_VENTOUSE_BAS);
    }

    @Override
    public void releasePompeVentouseBas() {
        vacuumController.off(POMPE_VENTOUSE_BAS);
    }

    @Override
    public void enablePompeVentouseHaut() {
        vacuumController.on(POMPE_VENTOUSE_HAUT);
    }

    @Override
    public void releasePompeVentouseHaut() {
        vacuumController.off(POMPE_VENTOUSE_HAUT);
    }

}
