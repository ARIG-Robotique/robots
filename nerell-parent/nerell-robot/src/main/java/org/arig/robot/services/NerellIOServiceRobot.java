package org.arig.robot.services;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.i2c.I2CBus;
import lombok.extern.slf4j.Slf4j;
import org.arig.pi4j.gpio.extension.pcf.PCF8574GpioProvider;
import org.arig.pi4j.gpio.extension.pcf.PCF8574Pin;
import org.arig.robot.constants.NerellConstantesI2C;
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

    // Input : Numerique 1
    private GpioPinDigitalInput calageBordureAvantGauche;
    private GpioPinDigitalInput calageBordureArriereDroit;
    private GpioPinDigitalInput inductifGauche;
    private GpioPinDigitalInput inductifDroit;
    private GpioPinDigitalInput pinceArriereCentre;
    private GpioPinDigitalInput pinceAvantCentre;
    private GpioPinDigitalInput calageBordureAvantDroit;
    private GpioPinDigitalInput calageBordureArriereGauche;

    // Input : Numerique 2
    private GpioPinDigitalInput tirette;
    private GpioPinDigitalInput pinceAvantDroit;
    private GpioPinDigitalInput pinceArriereDroit; // Fond du robot
    private GpioPinDigitalInput pinceArriereGauche;
    private GpioPinDigitalInput in2_5;
    private GpioPinDigitalInput in2_6;
    private GpioPinDigitalInput in2_7;
    private GpioPinDigitalInput pinceAvantGauche; // Bord du robot

    // Input : Numerique 3
    private GpioPinDigitalInput presenceArriereDroit;
    private GpioPinDigitalInput presenceArriereGauche;
    private GpioPinDigitalInput presenceArriereCentre;
    private GpioPinDigitalInput in3_4;
    private GpioPinDigitalInput presenceAvantDroit;
    private GpioPinDigitalInput presenceAvantGauche;
    private GpioPinDigitalInput presenceAvantCentre;
    private GpioPinDigitalInput in3_8;

    // Référence sur les PIN Output
    // ----------------------------

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
        calageBordureAvantGauche = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_00);
        calageBordureArriereDroit = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_01);
        inductifGauche = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_02);
        inductifDroit = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_03);
        pinceArriereCentre = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_04);
        pinceAvantCentre = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_05);
        calageBordureAvantDroit = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_06);
        calageBordureArriereGauche = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_07);

        // PCF2
        tirette = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_00);
        pinceAvantDroit = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_01);
        pinceArriereDroit = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_02);
        pinceArriereGauche = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_03);
        in2_5 = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_04);
        in2_6 = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_05);
        in2_7 = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_06);
        pinceAvantGauche = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_07);

        // PCF3
        presenceArriereDroit = gpio.provisionDigitalInputPin(pcf3, PCF8574Pin.GPIO_00);
        presenceArriereGauche = gpio.provisionDigitalInputPin(pcf3, PCF8574Pin.GPIO_01);
        presenceArriereCentre = gpio.provisionDigitalInputPin(pcf3, PCF8574Pin.GPIO_02);
        in3_4 = gpio.provisionDigitalInputPin(pcf3, PCF8574Pin.GPIO_03);
        presenceAvantDroit = gpio.provisionDigitalInputPin(pcf3, PCF8574Pin.GPIO_04);
        presenceAvantGauche = gpio.provisionDigitalInputPin(pcf3, PCF8574Pin.GPIO_05);
        presenceAvantCentre = gpio.provisionDigitalInputPin(pcf3, PCF8574Pin.GPIO_06);
        in3_8 = gpio.provisionDigitalInputPin(pcf3, PCF8574Pin.GPIO_07);
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
        return false;
    }

    // --------------------------------------------------------- //
    // -------------------------- INPUT ------------------------ //
    // --------------------------------------------------------- //

    // Numerique
    @Override
    public boolean in1_1() {
        return calageBordureAvantGauche.isLow();
    }

    @Override
    public boolean in1_2() {
        return calageBordureArriereDroit.isLow();
    }

    @Override
    public boolean in1_3() {
        return inductifGauche.isLow();
    }

    @Override
    public boolean in1_4() {
        return inductifDroit.isLow();
    }

    @Override
    public boolean in1_5() {
        return pinceArriereCentre.isLow();
    }

    @Override
    public boolean in1_6() {
        return pinceAvantCentre.isLow();
    }

    @Override
    public boolean in1_7() {
        return calageBordureAvantDroit.isLow();
    }

    @Override
    public boolean in1_8() {
        return calageBordureArriereGauche.isLow();
    }

    @Override
    public boolean in2_1() {
        return tirette.isLow();
    }

    @Override
    public boolean in2_2() {
        return pinceAvantDroit.isLow();
    }

    @Override
    public boolean in2_3() {
        return pinceArriereDroit.isLow();
    }

    @Override
    public boolean in2_4() {
        return pinceArriereGauche.isLow();
    }

    @Override
    public boolean in2_5() {
        return in2_5.isLow();
    }

    @Override
    public boolean in2_6() {
        return in2_6.isLow();
    }

    @Override
    public boolean in2_7() {
        return in2_7.isLow();
    }

    @Override
    public boolean in2_8() {
        return pinceAvantGauche.isLow();
    }

    @Override
    public boolean in3_1() {
        return presenceArriereDroit.isLow();
    }

    @Override
    public boolean in3_2() {
        return presenceArriereGauche.isLow();
    }

    @Override
    public boolean in3_3() {
        return presenceArriereCentre.isLow();
    }

    @Override
    public boolean in3_4() {
        return in3_4.isLow();
    }

    @Override
    public boolean in3_5() {
        return presenceAvantDroit.isLow();
    }

    @Override
    public boolean in3_6() {
        return presenceAvantGauche.isLow();
    }

    @Override
    public boolean in3_7() {
        return presenceAvantCentre.isLow();
    }

    @Override
    public boolean in3_8() {
        return in3_8.isLow();
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

    // ----------------------------------------------------------- //
    // -------------------------- BUSINESS ----------------------- //
    // ----------------------------------------------------------- //

}
