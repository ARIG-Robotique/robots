package org.arig.robot.services;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.i2c.I2CBus;
import lombok.extern.slf4j.Slf4j;
import org.arig.pi4j.gpio.extension.pcf.PCF8574GpioProvider;
import org.arig.pi4j.gpio.extension.pcf.PCF8574Pin;
import org.arig.robot.constants.IConstantesI2CNerell;
import org.arig.robot.model.NerellRobotStatus;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.system.capteurs.TCS34725ColorSensor.ColorData;

import java.io.IOException;

@Slf4j
@Service
public class IOService implements IIOService, InitializingBean, DisposableBean {

    @Autowired
    private NerellRobotStatus rs;

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
    private GpioPinDigitalInput inCalageBordureDroit;
    private GpioPinDigitalInput inCalageBordureGauche;
    private GpioPinDigitalInput inPresencePinceAvantLat1;
    private GpioPinDigitalInput inPresencePinceAvantLat2;
    private GpioPinDigitalInput inPresencePinceAvantLat3;
    private GpioPinDigitalInput inPresencePinceAvantLat4;
    private GpioPinDigitalInput inPresencePinceAvantSup1;
    private GpioPinDigitalInput inPresencePinceAvantSup2;
    private GpioPinDigitalInput inPresencePinceAvantSup3;
    private GpioPinDigitalInput inPresencePinceAvantSup4;
    private GpioPinDigitalInput inPresencePinceArriere1;
    private GpioPinDigitalInput inPresencePinceArriere2;
    private GpioPinDigitalInput inPresencePinceArriere3;
    private GpioPinDigitalInput inPresencePinceArriere4;
    private GpioPinDigitalInput inPresencePinceArriere5;

    // Référence sur les PIN Output
    // ----------------------------

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

        // Config PCF8574 //
        // -------------- //
        pcfAlim = new PCF8574GpioProvider(bus, IConstantesI2CNerell.PCF_ALIM_ADDRESS, true);
        pcf1 = new PCF8574GpioProvider(bus, IConstantesI2CNerell.PCF1_ADDRESS, true);
        pcf2 = new PCF8574GpioProvider(bus, IConstantesI2CNerell.PCF2_ADDRESS, true);

        // Alim
        inAu = gpio.provisionDigitalInputPin(pcfAlim, PCF8574Pin.GPIO_04);
        inAlimPuissance5V = gpio.provisionDigitalInputPin(pcfAlim, PCF8574Pin.GPIO_05);
        inAlimPuissance12V = gpio.provisionDigitalInputPin(pcfAlim, PCF8574Pin.GPIO_06);

        outAlimPuissance5V = gpio.provisionDigitalOutputPin(pcfAlim, PCF8574Pin.GPIO_00);
        outAlimPuissance12V = gpio.provisionDigitalOutputPin(pcfAlim, PCF8574Pin.GPIO_01);

        // PCF1
        inPresencePinceAvantLat1 = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_07);
        inPresencePinceAvantLat2 = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_06);
        inPresencePinceAvantLat3 = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_02);
        inPresencePinceAvantLat4 = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_03);
        inPresencePinceAvantSup1 = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_04);
        inPresencePinceAvantSup2 = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_05);
        inPresencePinceAvantSup3 = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_01);
        inPresencePinceAvantSup4 = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_00);

        // PCF2
        inTirette = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_02);
        inPresencePinceArriere1 = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_05);
        inPresencePinceArriere2 = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_07);
        inPresencePinceArriere3 = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_04);
        inPresencePinceArriere4 = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_06);
        inPresencePinceArriere5 = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_03);
        inCalageBordureGauche = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_01);
        inCalageBordureDroit = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_00);

    }

    @Override
    public void refreshAllPcf() {
        try {
            if (!pcf1.isShutdown()) {
                pcf1.readAll();
            }
        } catch (IOException e) {
            log.error("Erreur lecture PCF 1 : " + e.getMessage(), e);
        }

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
    public boolean presencePinceAvantLat1() {
        return inPresencePinceAvantLat1.isLow();
    }

    @Override
    public boolean presencePinceAvantLat2() {
        return inPresencePinceAvantLat2.isLow();
    }

    @Override
    public boolean presencePinceAvantLat3() {
        return inPresencePinceAvantLat3.isLow();
    }

    @Override
    public boolean presencePinceAvantLat4() {
        return inPresencePinceAvantLat4.isLow();
    }

    @Override
    public boolean presenceVentouse1() {
        return false; // TODO
    }

    @Override
    public boolean presenceVentouse2() {
        return false; // TODO
    }

    @Override
    public boolean presenceVentouse3() {
        return false; // TODO
    }

    @Override
    public boolean presenceVentouse4() {
        return false; // TODO
    }

    @Override
    public boolean presencePinceAvantSup1() {
        return inPresencePinceAvantSup1.isLow();
    }

    @Override
    public boolean presencePinceAvantSup2() {
        return inPresencePinceAvantSup2.isLow();
    }

    @Override
    public boolean presencePinceAvantSup3() {
        return inPresencePinceAvantSup3.isLow();
    }

    @Override
    public boolean presencePinceAvantSup4() {
        return inPresencePinceAvantSup4.isLow();
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
    public ECouleurBouee couleurBouee1() {
        return ECouleurBouee.INCONNU; // TODO
    }

    @Override
    public ECouleurBouee couleurBouee2() {
        return ECouleurBouee.INCONNU; // TODO
    }

    @Override
    public ECouleurBouee couleurBouee3() {
        return ECouleurBouee.INCONNU; // TODO
    }

    @Override
    public ECouleurBouee couleurBouee4() {
        return ECouleurBouee.INCONNU; // TODO
    }

    @Override
    public ColorData couleurRaw1() {
        return new ColorData().r(0).g(0).b(0); // TODO
    }

    @Override
    public ColorData couleurRaw2() {
        return new ColorData().r(0).g(0).b(0); // TODO
    }

    @Override
    public ColorData couleurRaw3() {
        return new ColorData().r(0).g(0).b(0); // TODO
    }

    @Override
    public ColorData couleurRaw4() {
        return new ColorData().r(0).g(0).b(0); // TODO
    }

    // --------------------------------------------------------- //
    // -------------------------- OUTPUT ----------------------- //
    // --------------------------------------------------------- //

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
    public void enablePompe1() {
        // TODO
    }

    @Override
    public void enablePompe2() {
        // TODO
    }

    @Override
    public void enablePompe3() {
        // TODO
    }

    @Override
    public void enablePompe4() {
        // TODO
    }

    @Override
    public void disablePompe1() {
        // TODO
    }

    @Override
    public void disablePompe2() {
        // TODO
    }

    @Override
    public void disablePompe3() {
        // TODO
    }

    @Override
    public void disablePompe4() {
        // TODO
    }
}
