package org.arig.robot.services;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.i2c.I2CBus;
import lombok.extern.slf4j.Slf4j;
import org.arig.pi4j.gpio.extension.pcf.PCF8574GpioProvider;
import org.arig.pi4j.gpio.extension.pcf.PCF8574Pin;
import org.arig.robot.constants.OdinConstantesI2C;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service("IOService")
public class OdinIOServiceRobot implements OdinIOService, InitializingBean, DisposableBean {

    @Autowired
    private I2CBus bus;

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


    // Référence sur les PIN Output
    // ----------------------------

    // GPIO

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

        // PCF1

        // PCF2

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
        return false; //inTirette.isLow();
    }

    // --------------------------------------------------------- //
    // -------------------------- INPUT ------------------------ //
    // --------------------------------------------------------- //

    // Calages

    @Override
    public boolean calagePriseProduitAvant() {
        return false;
    }

    @Override
    public boolean calagePriseProduitAvant(int mandatorySensors) {
        return false;
    }

    @Override
    public boolean calagePrisePotArriere() {
        return false;
    }

    @Override
    public boolean calagePrisePotArriere(int mandatorySensors) {
        return false;
    }

    @Override
    public boolean calageAvantGauche() {
        return false;
    }

    @Override
    public boolean calageAvantDroit() {
        return false;
    }

    @Override
    public boolean calageArriereGauche() {
        return false;
    }

    @Override
    public boolean calageArriereDroit() {
        return false;
    }

    // Numerique
    @Override
    public boolean pinceAvantGauche() {
        return false;
    }

    @Override
    public boolean pinceAvantCentre() {
        return false;
    }

    @Override
    public boolean pinceAvantDroite() {
        return false;
    }

    @Override
    public boolean pinceArriereGauche() {
        return false;
    }

    @Override
    public boolean pinceArriereCentre() {
        return false;
    }

    @Override
    public boolean pinceArriereDroite() {
        return false;
    }

    @Override
    public boolean presenceAvantGauche() {
        return false;
    }

    @Override
    public boolean presenceAvantCentre() {
        return false;
    }

    @Override
    public boolean presenceAvantDroite() {
        return false;
    }

    @Override
    public boolean presenceArriereGauche() {
        return false;
    }

    @Override
    public boolean presenceArriereCentre() {
        return false;
    }

    @Override
    public boolean presenceArriereDroite() {
        return false;
    }

    @Override
    public boolean inductifGauche() {
        return false;
    }

    @Override
    public boolean inductifDroit() {
        return false;
    }

    // Analogique


    // --------------------------------------------------------- //
    // -------------------------- OUTPUT ----------------------- //
    // --------------------------------------------------------- //

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

    @Override
    public void enableElectroAimant() {

    }

    @Override
    public void disableElectroAiment() {

    }

    @Override
    public void tournePanneauArriere() {

    }

    @Override
    public void tournePanneauAvant() {

    }

    @Override
    public void stopTournePanneau() {

    }

    // ----------------------------------------------------------- //
    // -------------------------- BUSINESS ----------------------- //
    // ----------------------------------------------------------- //

}
