package org.arig.robot.services;

import com.pi4j.gpio.extension.pcf.PCF8574GpioProvider;
import com.pi4j.gpio.extension.pcf.PCF8574Pin;
import com.pi4j.io.gpio.*;
import com.pi4j.io.i2c.I2CBus;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesI2C;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.Team;
import org.arig.robot.system.capteurs.TCS34725ColorSensor;
import org.arig.robot.system.capteurs.TCS34725ColorSensor.ColorData;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author gdepuille on 23/04/15.
 */
@Slf4j
@Service
public class IOService implements IIOService, InitializingBean, DisposableBean {

    @Autowired
    private RobotStatus rs;

    @Autowired
    private I2CBus bus;

    @Autowired
    @Qualifier("frontColorSensor")
    private TCS34725ColorSensor frontColorSensor;

    private GpioController gpio;
    private PCF8574GpioProvider pcfAlim;
    private PCF8574GpioProvider pcf1;
    private PCF8574GpioProvider pcf2;
    private PCF8574GpioProvider pcf3;

    // Référence sur les PIN Input
    private GpioPinDigitalInput inIrqAlim;
    private GpioPinDigitalInput inIrqPcf1;
    private GpioPinDigitalInput inIrq1;
    private GpioPinDigitalInput inIrq3;
    private GpioPinDigitalInput inIrq4;
    private GpioPinDigitalInput inIrq5;
    private GpioPinDigitalInput inIrq6;
    private GpioPinDigitalInput inAlimPuissance5V;
    private GpioPinDigitalInput inAlimPuissance8V;
    private GpioPinDigitalInput inAlimPuissance12V;
    private GpioPinDigitalInput inAu;
    private GpioPinDigitalInput inEquipe;
    private GpioPinDigitalInput inTirette;
    private GpioPinDigitalInput inPresencePinceCentre;
    private GpioPinDigitalInput inPresencePinceDroite;
    private GpioPinDigitalInput inPresenceFusee;
    private GpioPinDigitalInput inBordureAvant;
    private GpioPinDigitalInput inComptageMagasin;
    private GpioPinDigitalInput inPresenceDevidoir;
    private GpioPinDigitalInput inPresenceRouleaux;
    private GpioPinDigitalInput inFinCourseGlissiereGauche;
    private GpioPinDigitalInput inFinCourseGlissiereDroite;
    private GpioPinDigitalInput inPresenceBalleAspiration;
    private GpioPinDigitalInput inBordureArriereDroite;
    private GpioPinDigitalInput inBordureArriereGauche;
    private GpioPinDigitalInput inPresenceBaseLunaireDroite;
    private GpioPinDigitalInput inPresenceBaseLunaireGauche;

    // Référence sur les PIN Output
    private GpioPinDigitalOutput outAlimPuissance5V;
    private GpioPinDigitalOutput outAlimPuissance8V;
    private GpioPinDigitalOutput outAlimPuissance12V;
    private GpioPinDigitalOutput outCmdLedCapteurRGB;
    private GpioPinDigitalOutput outLedRGB_R;
    private GpioPinDigitalOutput outLedRGB_G;
    private GpioPinDigitalOutput outLedRGB_B;
    private GpioPinDigitalOutput outElectroVanne;
    private GpioPinDigitalOutput outPompeAVide;

    @Override
    public void destroy() throws Exception {
        pcfAlim.shutdown();
        pcf1.shutdown();
        pcf2.shutdown();
        pcf3.shutdown();
        gpio.shutdown();
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        // Config des IO raspi //
        // ------------------- //
        gpio = GpioFactory.getInstance();

        // Inputs
        inEquipe = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02);
        inIrqAlim = gpio.provisionDigitalInputPin(RaspiPin.GPIO_07);
        inIrqPcf1 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_04);
        inIrq1 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_00);
        inIrq3 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_01);
        inIrq4 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_16);
        inIrq5 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_15);
        inIrq6 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_06);

        // Output
        outCmdLedCapteurRGB = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_05, PinState.LOW);
        outLedRGB_R = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_12, PinState.LOW);
        outLedRGB_G = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_13, PinState.LOW);
        outLedRGB_B = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_14, PinState.LOW);

        // Config PCF8574 //
        // -------------- //
        // TODO Config IRQ
        pcfAlim = new PCF8574GpioProvider(bus, IConstantesI2C.PCF_ALIM_ADDRESS);
        pcf1 = new PCF8574GpioProvider(bus, IConstantesI2C.PCF1_ADDRESS);
        pcf2 = new PCF8574GpioProvider(bus, IConstantesI2C.PCF2_ADDRESS);
        pcf3 = new PCF8574GpioProvider(bus, IConstantesI2C.PCF3_ADDRESS, true);

        // Alim
        inAlimPuissance5V = gpio.provisionDigitalInputPin(pcfAlim, PCF8574Pin.GPIO_05);
        inAlimPuissance8V = gpio.provisionDigitalInputPin(pcfAlim, PCF8574Pin.GPIO_07);
        inAlimPuissance12V = gpio.provisionDigitalInputPin(pcfAlim, PCF8574Pin.GPIO_06);
        inAu = gpio.provisionDigitalInputPin(pcfAlim, PCF8574Pin.GPIO_04);

        outAlimPuissance5V = gpio.provisionDigitalOutputPin(pcfAlim, PCF8574Pin.GPIO_00);
        outAlimPuissance8V = gpio.provisionDigitalOutputPin(pcfAlim, PCF8574Pin.GPIO_02);
        outAlimPuissance12V = gpio.provisionDigitalOutputPin(pcfAlim, PCF8574Pin.GPIO_01);

        // PCF1
        inTirette = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_00);
        inBordureAvant = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_01);
        inComptageMagasin = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_02);
        inPresenceDevidoir = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_03);
        inFinCourseGlissiereDroite = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_04);
        inFinCourseGlissiereGauche = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_05);
        inPresencePinceCentre = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_06);
        inPresencePinceDroite = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_07);

        // PCF2
        inPresenceBaseLunaireDroite= gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_00);
        inPresenceBaseLunaireGauche = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_01);
        inPresenceBalleAspiration = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_02);
        inBordureArriereDroite = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_03);
        inPresenceRouleaux = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_04);
        inPresenceFusee = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_05);
        inBordureArriereGauche = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_06);

        // PCF3
        outElectroVanne = gpio.provisionDigitalOutputPin(pcf3, PCF8574Pin.GPIO_00);
        outPompeAVide = gpio.provisionDigitalOutputPin(pcf3, PCF8574Pin.GPIO_01);

        // Etat initial des IOs
        disableLedCapteurCouleur();
        clearColorLedRGB();
        disableElectroVanne();
        disablePompeAVide();
    }

    // --------------------------------------------------------- //
    // --------------------- CHECK PREPARATION ----------------- //
    // --------------------------------------------------------- //

    @Override
    public Team equipe() {
        rs.setTeam(inEquipe.isHigh() ? Team.BLEU : Team.JAUNE);
        return rs.getTeam();
    }

    // --------------------------------------------------------- //
    // -------------------------- INPUT ------------------------ //
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
    public boolean alimPuissance8VOk() {
        return inAlimPuissance8V.isHigh();
    }

    @Override
    public boolean alimPuissance12VOk() {
        return inAlimPuissance12V.isHigh();
    }

    @Override
    public boolean tirette() {
        return inTirette.isLow();
    }

    @Override
    public boolean ledCapteurCouleur() {
        return outCmdLedCapteurRGB.isHigh();
    }

    @Override
    public ColorData frontColor() {
        return frontColorSensor.getColorData();
    }

    // --------------------------------------------------------- //
    // -------------------------- OUTPUT ----------------------- //
    // --------------------------------------------------------- //

    @Override
    public void colorLedRGBKo() {
        outLedRGB_R.high();
        outLedRGB_G.low();
        outLedRGB_B.low();
    }

    @Override
    public void colorLedRGBOk() {
        outLedRGB_R.low();
        outLedRGB_G.high();
        outLedRGB_B.low();
    }

    @Override
    public void teamColorLedRGB() {
        if (rs.getTeam() == Team.BLEU) {
            outLedRGB_R.low();
            outLedRGB_G.low();
            outLedRGB_B.high();
        } else if (rs.getTeam() == Team.JAUNE) {
            outLedRGB_R.high();
            outLedRGB_G.high();
            outLedRGB_B.low();
        } else {
            clearColorLedRGB();
        }
    }

    @Override
    public void clearColorLedRGB() {
        outLedRGB_R.low();
        outLedRGB_G.low();
        outLedRGB_B.low();
    }

    @Override
    public void enableLedCapteurCouleur() {
        outCmdLedCapteurRGB.high();
    }

    @Override
    public void disableLedCapteurCouleur() {
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
    public void enableAlim8VPuissance() {
        log.info("Activation puissance 8V");
        outAlimPuissance8V.low();
    }

    @Override
    public void disableAlim8VPuissance() {
        log.info("Desactivation puissance 8V");
        outAlimPuissance8V.high();
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

    @Override
    public void enableElectroVanne() {
        outElectroVanne.low();
    }

    @Override
    public void disableElectroVanne() {
        outElectroVanne.high();
    }

    @Override
    public void enablePompeAVide() {
        outPompeAVide.low();
    }

    @Override
    public void disablePompeAVide() {
        outPompeAVide.high();
    }
}
