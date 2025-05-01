package org.arig.robot.services;

import com.pi4j.gpio.extension.pca.PCA9685GpioProvider;
import com.pi4j.gpio.extension.pca.PCA9685Pin;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.i2c.I2CBus;
import lombok.extern.slf4j.Slf4j;
import org.arig.pi4j.gpio.extension.pcf.PCF8574GpioProvider;
import org.arig.pi4j.gpio.extension.pcf.PCF8574Pin;
import org.arig.robot.constants.NerellConstantesI2C;
import org.arig.robot.filters.average.BooleanValueAverage;
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

    @Autowired
    private PCA9685GpioProvider pca9695;

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

    // Input : GPIO Raspi
    private GpioPinDigitalInput tirette;

    // Input : Alimentation
    private GpioPinDigitalInput inAu;

    // Input : Numerique 1
    private GpioPinDigitalInput calageAvantGauche;
    private GpioPinDigitalInput calageAvantDroit;
    private GpioPinDigitalInput calageArriereGauche;
    private GpioPinDigitalInput calageArriereDroit;
    private GpioPinDigitalInput stockAvantGauche;
    private GpioPinDigitalInput stockAvantDroit;
    private GpioPinDigitalInput stockArriereGauche;
    private GpioPinDigitalInput stockArriereDroit;

    // Input : Numerique 2
    private GpioPinDigitalInput pinceAvantGauche;
    private GpioPinDigitalInput pinceAvantDroite;
    private GpioPinDigitalInput pinceArriereGauche;
    private GpioPinDigitalInput pinceArriereDroite;
    private GpioPinDigitalInput tiroirAvantHaut;
    private GpioPinDigitalInput tiroirAvantBas;
    private GpioPinDigitalInput tiroirArriereHaut;
    private GpioPinDigitalInput tiroirArriereBas;

    // Input : Virtual averaged

    // Référence sur les PIN Output
    // ----------------------------

    // PCF
    private GpioPinDigitalOutput outAlimPuissanceServos;
    private GpioPinDigitalOutput outAlimPuissanceMoteurs;

    @Override
    public void destroy() throws Exception {
        for (PCF8574GpioProvider pcf : new PCF8574GpioProvider[]{pcfAlim, pcf1, pcf2}) {
            try {
                if (pcf != null) {
                    pcf.shutdown();
                }
            } catch (Exception e) {
                log.warn("Problème de shutdown du PCF : {} #{}", e.getMessage(), pcf.i2cAddress());
            }
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

        tirette = gpio.provisionDigitalInputPin(RaspiPin.GPIO_04);

        // Config PCF8574 //
        // -------------- //
        pcfAlim = new PCF8574GpioProvider(bus, NerellConstantesI2C.PCF_ALIM_ADDRESS, true);
        pcf1 = new PCF8574GpioProvider(bus, NerellConstantesI2C.PCF1_ADDRESS, true);
        pcf2 = new PCF8574GpioProvider(bus, NerellConstantesI2C.PCF2_ADDRESS, true);

        // Alim
        inAu = gpio.provisionDigitalInputPin(pcfAlim, PCF8574Pin.GPIO_00);

        outAlimPuissanceServos = gpio.provisionDigitalOutputPin(pcfAlim, PCF8574Pin.GPIO_03);
        outAlimPuissanceMoteurs = gpio.provisionDigitalOutputPin(pcfAlim, PCF8574Pin.GPIO_04);

        tirette = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_00);

        // PCF1
        calageAvantGauche = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_00);
        calageArriereDroit = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_01);
        calageAvantDroit = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_02);
        calageArriereGauche = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_03);
        stockAvantGauche = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_04);
        stockAvantDroit = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_05);
        stockArriereGauche = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_06);
        stockArriereDroit = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_07);

        // PCF2
        pinceAvantGauche = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_00);
        pinceAvantDroite = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_01);
        pinceArriereGauche = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_02);
        pinceArriereDroite = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_03);
        tiroirAvantHaut = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_04);
        tiroirAvantBas = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_05);
        tiroirArriereHaut = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_06);
        tiroirArriereBas = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_07);
    }

    @Override
    public void refreshAllIO() {
        for (PCF8574GpioProvider pcf : new PCF8574GpioProvider[]{pcfAlim, pcf1, pcf2}) {
            try {
                if (!pcf.isShutdown()) {
                    pcf.readAll();
                }
            } catch (IOException e) {
                log.error("Erreur lecture {} -> #{}", e.getMessage(), pcf.i2cAddress());
            }
        }

        // Refresh virtual IOs

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
        if (mandatorySensors > 2) {
            throw new IllegalArgumentException("Le nombre de capteurs avant obligatoires ne peut pas être supérieur à 2");
        }
        int count = pinceAvantGauche(true) ? 1 : 0;
        count += pinceAvantDroite(true) ? 1 : 0;
        return count >= mandatorySensors;
    }

    @Override
    public boolean calagePriseProduitArriere() {
        return calagePriseProduitArriere(1);
    }

    @Override
    public boolean calagePriseProduitArriere(int mandatorySensors) {
        if (mandatorySensors > 2) {
            throw new IllegalArgumentException("Le nombre de capteurs arrière obligatoires ne peut pas être supérieur à 2");
        }
        int count = pinceArriereGauche(true) ? 1 : 0;
        count += pinceArriereDroite(true) ? 1 : 0;
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
    public boolean pinceAvantGauche(boolean expectedSimulator) {
        return pinceAvantGauche.isLow();
    }

    @Override
    public boolean pinceAvantDroite(boolean expectedSimulator) {
        return pinceAvantDroite.isLow();
    }

    @Override
    public boolean pinceArriereGauche(boolean expectedSimulator) {
        return pinceArriereGauche.isLow();
    }

    @Override
    public boolean pinceArriereDroite(boolean expectedSimulator) {
        return pinceArriereDroite.isLow();
    }

    @Override
    public boolean stockAvantGauche(boolean expectedSimulator) {
        return stockAvantGauche.isLow();
    }

    @Override
    public boolean stockAvantDroite(boolean expectedSimulator) {
        return stockAvantDroit.isLow();
    }

    @Override
    public boolean stockArriereGauche(boolean expectedSimulator) {
        return stockArriereGauche.isLow();
    }

    @Override
    public boolean stockArriereDroite(boolean expectedSimulator) {
        return stockArriereDroit.isLow();
    }

    @Override
    public boolean tiroirAvantHaut(boolean expectedSimulator) {
        return tiroirAvantHaut.isLow();
    }

    @Override
    public boolean tiroirAvantBas(boolean expectedSimulator) {
        return tiroirAvantBas.isLow();
    }

    @Override
    public boolean tiroirArriereHaut(boolean expectedSimulator) {
        return tiroirArriereHaut.isLow();
    }

    @Override
    public boolean tiroirArriereBas(boolean expectedSimulator) {
        return tiroirArriereBas.isLow();
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
