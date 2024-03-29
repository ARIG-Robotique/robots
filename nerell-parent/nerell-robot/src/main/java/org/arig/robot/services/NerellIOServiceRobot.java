package org.arig.robot.services;

import com.pi4j.gpio.extension.pca.PCA9685GpioProvider;
import com.pi4j.gpio.extension.pca.PCA9685Pin;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.i2c.I2CBus;
import lombok.extern.slf4j.Slf4j;
import org.arig.pi4j.gpio.extension.pcf.PCF8574GpioProvider;
import org.arig.pi4j.gpio.extension.pcf.PCF8574Pin;
import org.arig.robot.constants.NerellConstantesI2C;
import org.arig.robot.system.motors.PCA9685ToTB6612Motor;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service("IOService")
public class NerellIOServiceRobot implements NerellIOService, InitializingBean, DisposableBean {

    @Autowired
    private I2CBus bus;

    // Controlleur GPIO
    private GpioController gpio;
    private PCF8574GpioProvider pcfAlim;
    private PCF8574GpioProvider pcf1;
    private PCF8574GpioProvider pcf2;
    private PCF8574GpioProvider pcf3;

    @Autowired
    private PCA9685GpioProvider pca9695;
    @Autowired
    private PCA9685ToTB6612Motor solarWheelMotor;

    // Référence sur les PIN Inputs
    // ----------------------------

    // IRQ
    // private GpioPinDigitalInput inIrqAlim;
    // private GpioPinDigitalInput inIrqPcf1;
    // private GpioPinDigitalInput inIrq1;
    // private GpioPinDigitalInput inIrq3;
    // private GpioPinDigitalInput inIrq4;
    // private GpioPinDigitalInput inIrq5;
    // private GpioPinDigitalInput inIrq6;

    // Input : Alimentation
    private GpioPinDigitalInput inAu;

    // Input : Numerique 1
    private GpioPinDigitalInput calageAvantGauche;
    private GpioPinDigitalInput calageAvantDroit;
    private GpioPinDigitalInput calageArriereGauche;
    private GpioPinDigitalInput calageArriereDroit;
    private GpioPinDigitalInput inductifGauche;
    private GpioPinDigitalInput inductifDroit;
    private GpioPinDigitalInput pinceAvantCentre;
    private GpioPinDigitalInput pinceArriereCentre;

    // Input : Numerique 2
    private GpioPinDigitalInput tirette;
    private GpioPinDigitalInput pinceAvantGauche;
    private GpioPinDigitalInput pinceAvantDroit;
    private GpioPinDigitalInput pinceArriereGauche;
    private GpioPinDigitalInput pinceArriereDroit;

    // private GpioPinDigitalInput in2_5;
    // private GpioPinDigitalInput in2_6;
    // private GpioPinDigitalInput in2_7;

    // Input : Numerique 3
    private GpioPinDigitalInput presenceAvantGauche;
    private GpioPinDigitalInput presenceAvantCentre;
    private GpioPinDigitalInput presenceAvantDroit;
    private GpioPinDigitalInput presenceArriereGauche;
    private GpioPinDigitalInput presenceArriereCentre;
    private GpioPinDigitalInput presenceArriereDroit;

    // private GpioPinDigitalInput in3_4;
    // private GpioPinDigitalInput in3_8;

    // Référence sur les PIN Output
    // ----------------------------

    // PCF
    private GpioPinDigitalOutput outAlimPuissanceServos;
    private GpioPinDigitalOutput outAlimPuissanceMoteurs;

    // PCA 9685
    private final Pin eaIn1Pin = PCA9685Pin.PWM_14;
    private final Pin eaIn2Pin = PCA9685Pin.PWM_13;
    private final Pin eaPwmPin = PCA9685Pin.PWM_12;

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
            log.warn("Problème de shutdown du {} : {}", NerellConstantesI2C.PCF1_DEVICE_NAME, e.getMessage());
        }
        try {
            if (pcf2 != null) {
                pcf2.shutdown();
            }
        } catch (Exception e) {
            log.warn("Problème de shutdown du {} : {}", NerellConstantesI2C.PCF2_DEVICE_NAME, e.getMessage());
        }
        try {
            if (pcf3 != null) {
                pcf3.shutdown();
            }
        } catch (Exception e) {
            log.warn("Problème de shutdown du {} : {}", NerellConstantesI2C.PCF3_DEVICE_NAME, e.getMessage());
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

        // Config PCF8574 //
        // -------------- //
        pcfAlim = new PCF8574GpioProvider(bus, NerellConstantesI2C.PCF_ALIM_ADDRESS, true);
        pcf1 = new PCF8574GpioProvider(bus, NerellConstantesI2C.PCF1_ADDRESS, true);
        pcf2 = new PCF8574GpioProvider(bus, NerellConstantesI2C.PCF2_ADDRESS, true);
        pcf3 = new PCF8574GpioProvider(bus, NerellConstantesI2C.PCF3_ADDRESS, true);

        // Alim
        inAu = gpio.provisionDigitalInputPin(pcfAlim, PCF8574Pin.GPIO_00);

        outAlimPuissanceServos = gpio.provisionDigitalOutputPin(pcfAlim, PCF8574Pin.GPIO_03);
        outAlimPuissanceMoteurs = gpio.provisionDigitalOutputPin(pcfAlim, PCF8574Pin.GPIO_04);

        // PCF1
        calageAvantGauche = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_00);
        calageArriereDroit = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_01);
        inductifGauche = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_02);
        inductifDroit = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_03);
        pinceArriereCentre = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_04);
        pinceAvantCentre = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_05);
        calageAvantDroit = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_06);
        calageArriereGauche = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_07);

        // PCF2
        tirette = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_00);
        pinceAvantDroit = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_01);
        pinceArriereDroit = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_02);
        pinceArriereGauche = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_03);
        //in2_5 = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_04);
        //in2_6 = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_05);
        //in2_7 = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_06);
        pinceAvantGauche = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_07);

        // PCF3
        presenceArriereDroit = gpio.provisionDigitalInputPin(pcf3, PCF8574Pin.GPIO_00);
        presenceArriereGauche = gpio.provisionDigitalInputPin(pcf3, PCF8574Pin.GPIO_01);
        presenceArriereCentre = gpio.provisionDigitalInputPin(pcf3, PCF8574Pin.GPIO_02);
        //in3_4 = gpio.provisionDigitalInputPin(pcf3, PCF8574Pin.GPIO_03);
        presenceAvantDroit = gpio.provisionDigitalInputPin(pcf3, PCF8574Pin.GPIO_04);
        presenceAvantGauche = gpio.provisionDigitalInputPin(pcf3, PCF8574Pin.GPIO_05);
        presenceAvantCentre = gpio.provisionDigitalInputPin(pcf3, PCF8574Pin.GPIO_06);
        //in3_8 = gpio.provisionDigitalInputPin(pcf3, PCF8574Pin.GPIO_07);

    }

    @Override
    public void refreshAllIO() {
        try {
            if (!pcf1.isShutdown()) {
                pcf1.readAll();
            }
        } catch (IOException e) {
            log.error("Erreur lecture " + NerellConstantesI2C.PCF1_DEVICE_NAME + " : " + e.getMessage(), e);
        }

        try {
            if (!pcf2.isShutdown()) {
                pcf2.readAll();
            }
        } catch (IOException e) {
            log.error("Erreur lecture " + NerellConstantesI2C.PCF2_DEVICE_NAME + " : " + e.getMessage(), e);
        }

        try {
            if (!pcf3.isShutdown()) {
                pcf3.readAll();
            }
        } catch (IOException e) {
            log.error("Erreur lecture " + NerellConstantesI2C.PCF3_DEVICE_NAME + " : " + e.getMessage(), e);
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
    public boolean tirette() {
        return tirette.isLow();
    }

    // --------------------------------------------------------- //
    // -------------------------- INPUT ------------------------ //
    // --------------------------------------------------------- //

    // Calages
    @Override
    public boolean calagePriseProduitAvant() {
        return calagePriseProduitAvant(1);
    }

    @Override
    public boolean calagePriseProduitAvant(int mandatorySensors) {
        if (mandatorySensors > 3) {
            throw new IllegalArgumentException("Le nombre de capteurs avant obligatoires ne peut pas être supérieur à 3");
        }
        int count = presenceAvantGauche() ? 1 : 0;
        count += presenceAvantCentre() ? 1 : 0;
        count += presenceAvantDroite() ? 1 : 0;
        return count >= mandatorySensors;
    }

    @Override
    public boolean calagePrisePotArriere() {
        return calagePrisePotArriere(1);
    }

    @Override
    public boolean calagePrisePotArriere(int mandatorySensors) {
        if (mandatorySensors > 3) {
            throw new IllegalArgumentException("Le nombre de capteurs arrière obligatoires ne peut pas être supérieur à 3");
        }
        int count = presenceArriereGauche() ? 1 : 0;
        count += presenceArriereCentre() ? 1 : 0;
        count += presenceArriereDroite() ? 1 : 0;
        return count >= mandatorySensors;
    }

    @Override
    public boolean calageAvantGauche() {
        return calageAvantGauche.isLow();
    }

    @Override
    public boolean calageAvantDroit() {
        return calageAvantDroit.isLow();
    }

    @Override
    public boolean calageArriereGauche() {
        return calageArriereGauche.isLow();
    }

    @Override
    public boolean calageArriereDroit() {
        return calageArriereDroit.isLow();
    }

    // Numerique

    @Override
    public boolean presenceAvantGauche() {
        return presenceAvantGauche.isLow();
    }

    @Override
    public boolean presenceAvantCentre() {
        return presenceAvantCentre.isLow();
    }

    @Override
    public boolean presenceAvantDroite() {
        return presenceAvantDroit.isLow();
    }

    @Override
    public boolean presenceArriereGauche() {
        return presenceArriereGauche.isLow();
    }

    @Override
    public boolean presenceArriereCentre() {
        return presenceArriereCentre.isLow();
    }

    @Override
    public boolean presenceArriereDroite() {
        return presenceArriereDroit.isLow();
    }

    @Override
    public boolean inductifGauche() {
        return inductifGauche.isLow();
    }

    @Override
    public boolean inductifDroit() {
        return inductifDroit.isLow();
    }

    @Override
    public boolean pinceAvantGauche() {
        return pinceAvantGauche.isLow();
    }

    @Override
    public boolean pinceAvantCentre() {
        return pinceAvantCentre.isLow();
    }

    @Override
    public boolean pinceAvantDroite() {
        return pinceAvantDroit.isLow();
    }

    @Override
    public boolean pinceArriereGauche() {
        return pinceArriereGauche.isLow();
    }

    @Override
    public boolean pinceArriereCentre() {
        return pinceArriereCentre.isLow();
    }

    @Override
    public boolean pinceArriereDroite() {
        return pinceArriereDroit.isLow();
    }

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
        log.info("Activation electro aimant");
        pca9695.setAlwaysOn(eaIn1Pin);
        pca9695.setAlwaysOff(eaIn2Pin);
        pca9695.setAlwaysOn(eaPwmPin);
    }

    @Override
    public void disableElectroAiment() {
        log.info("Desactivation electro aimant");
        pca9695.setAlwaysOff(eaIn1Pin);
        pca9695.setAlwaysOff(eaIn2Pin);
        pca9695.setAlwaysOff(eaPwmPin);
    }

    @Override
    public void tournePanneauArriere() {
        log.info("Demarrage du moteur de rotation du panneau vers l'arrière");
        solarWheelMotor.speed(2048);
    }

    @Override
    public void tournePanneauAvant() {
        log.info("Demarrage du moteur de rotation du panneau vers l'avant");
        solarWheelMotor.speed(-2048);
    }

    @Override
    public void stopTournePanneau() {
        log.info("Arret du moteur de rotation du panneau");
        solarWheelMotor.stop();
    }

    // ----------------------------------------------------------- //
    // -------------------------- BUSINESS ----------------------- //
    // ----------------------------------------------------------- //

}
