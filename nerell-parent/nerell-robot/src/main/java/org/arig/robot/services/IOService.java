package org.arig.robot.services;

import com.pi4j.gpio.extension.pcf.PCF8574GpioProvider;
import com.pi4j.gpio.extension.pcf.PCF8574Pin;
import com.pi4j.io.gpio.*;
import com.pi4j.io.i2c.I2CBus;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesI2C;
import org.arig.robot.constants.IConstantesI2CAdc;
import org.arig.robot.exception.I2CException;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.Team;
import org.arig.robot.model.monitor.MonitorTimeSerie;
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.arig.robot.system.capteurs.I2CAdcAnalogInput;
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
    private I2CAdcAnalogInput i2cAdc;

    @Autowired
    private IMonitoringWrapper monitoring;

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
        inPresenceBaseLunaireDroite = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_00);
        inPresenceBaseLunaireGauche = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_01);
        inPresenceBalleAspiration = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_02);
        inBordureArriereDroite = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_03);
        inPresenceRouleaux = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_04);
        inPresenceFusee = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_05);
        inBordureArriereGauche = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_06);

        // PCF3
        outElectroVanne = gpio.provisionDigitalOutputPin(pcf3, PCF8574Pin.GPIO_01);
        outPompeAVide = gpio.provisionDigitalOutputPin(pcf3, PCF8574Pin.GPIO_02);

        // Etat initial des IOs
        outAlimPuissance8V.high(); // Désactivé
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
        boolean result = inAu.isLow();
        log.info("AU present : {}", result);
        return result;
    }

    @Override
    public boolean alimPuissance5VOk() {
        boolean result = inAlimPuissance5V.isHigh();
        log.info("Puissance 5V present : {}", result);
        return result;
    }

    @Override
    public boolean alimPuissance12VOk() {
        boolean result = inAlimPuissance12V.isHigh();
        log.info("Puisance 12V present : {}", result);
        return result;
    }

    @Override
    public boolean tirette() {
        return inTirette.isLow();
    }

    @Override
    public boolean ledCapteurCouleur() {
        boolean result = outCmdLedCapteurRGB.isHigh();
        log.info("LED capteur couleur allumé : {}", result);
        return result;
    }

    @Override
    public boolean bordureAvant() {
        boolean result = inBordureAvant.isLow();
        log.info("Présence bordure avant : {}", result);
        return result;
    }

    @Override
    public boolean bordureArriereDroite() {
        boolean result = inBordureArriereDroite.isLow();
        log.info("Présence bordure arrière droite : {}", result);
        return result;
    }

    @Override
    public boolean bordureArriereGauche() {
        boolean result = inBordureArriereGauche.isLow();
        log.info("Présence bordure arrière gauche : {}", result);
        return result;
    }

    @Override
    public boolean presenceEntreeMagasin() {
        boolean result = inComptageMagasin.isLow();
        log.info("Présence comptage magasin : {}", result);
        return result;
    }

    @Override
    public boolean presenceDevidoir() {
        boolean result = inPresenceDevidoir.isLow();
        log.info("Présence dévidoir : {}", result);
        return result;
    }

    @Override
    public boolean presencePinceDroite() {
        boolean result = inPresencePinceDroite.isLow();
        log.info("Presence pince droite : {}", result);
        return result;
    }

    @Override
    public boolean presencePinceCentre() {
        boolean result = inPresencePinceCentre.isLow();
        log.info("Presence pince centre : {}", result);
        return result;
    }

    @Override
    public boolean presenceBaseLunaireDroite() {
        boolean result = inPresenceBaseLunaireDroite.isLow();
        log.info("Presence base lunaire droite : {}", result);
        return result;
    }

    @Override
    public boolean presenceBaseLunaireGauche() {
        boolean result = inPresenceBaseLunaireGauche.isLow();
        log.info("Presence base lunaire gauche : {}", result);
        return result;
    }

    @Override
    public boolean presenceBallesAspiration() {
        boolean result = inPresenceBalleAspiration.isLow();
        log.info("Presence balle aspiration : {}", result);
        return result;
    }

    @Override
    public boolean presenceRouleaux() {
        boolean result = inPresenceRouleaux.isLow();
        log.info("Présence rouleaux : {}", result);
        return result;
    }

    @Override
    public boolean presenceFusee() {
        boolean result = inPresenceFusee.isLow();
        log.info("Presence fusée : {}", result);
        return result;
    }

    @Override
    public boolean presenceModuleDansBras() {
        boolean result;
        try {
            int analogValue = i2cAdc.readCapteurValue(IConstantesI2CAdc.VACUOSTAT);
            result = analogValue > IConstantesI2CAdc.VACUOSTAT_SEUIL;
            log.info("Lecture capteur de vide {}", analogValue);
        } catch (I2CException e) {
            result = false;
        }
        log.info("Présence module dans bras : {}", result);
        return result;
    }

    @Override
    public boolean finCourseGlissiereDroite() {
        boolean result = inFinCourseGlissiereDroite.isLow();
        log.info("Fin de course glissiere droite : {}", result);
        return result;
    }

    @Override
    public boolean finCourseGlissiereGauche() {
        boolean result = inFinCourseGlissiereGauche.isLow();
        log.info("Fin de course glissiere gauche : {}", result);
        return result;
    }

    @Override
    public ColorData frontColor() {
        return frontColorSensor.getColorData();
    }

    @Override
    public Team getTeamColorFromSensor() {
        ColorData c = frontColorSensor.getColorData();

        MonitorTimeSerie mts = new MonitorTimeSerie();
        mts.tableName("couleur");
        mts.addField("r", c.r());
        mts.addField("g", c.g());
        mts.addField("b", c.b());
        monitoring.addTimeSeriePoint(mts);

        int delta = 42;

        Team result;
        if (c.b() - c.r() > delta && c.b() - c.g() > delta / 2) {
            result = Team.BLEU;
        } else if (c.r() - c.b() > delta && c.g() - c.b() > delta && Math.abs(c.r() - c.g()) < delta) {
            result = Team.JAUNE;
        } else {
            result = Team.UNKNOWN;
        }
        log.info("Lecture capteur couleur R: {}, G: {}, B: {}, Team: {}", c.r(), c.g(), c.b(), result.name());
        return result;
    }

    // --------------------------------------------------------- //
    // -------------------------- OUTPUT ----------------------- //
    // --------------------------------------------------------- //

    @Override
    public void colorLedRGBKo() {
        log.info("Led RGB couleur KO");
        outLedRGB_R.high();
        outLedRGB_G.low();
        outLedRGB_B.low();
    }

    @Override
    public void colorLedRGBOk() {
        log.info("Led RGB couleur OK");
        outLedRGB_R.low();
        outLedRGB_G.high();
        outLedRGB_B.low();
    }

    @Override
    public void teamColorLedRGB() {
        if (rs.getTeam() == Team.BLEU) {
            log.info("Led RGB couleur Team BLEU");
            outLedRGB_R.low();
            outLedRGB_G.low();
            outLedRGB_B.high();
        } else if (rs.getTeam() == Team.JAUNE) {
            log.info("Led RGB couleur Team JAUNE");
            outLedRGB_R.high();
            outLedRGB_G.high();
            outLedRGB_B.low();
        } else {
            clearColorLedRGB();
        }
    }

    @Override
    public void clearColorLedRGB() {
        log.info("Led RGB eteinte");
        outLedRGB_R.low();
        outLedRGB_G.low();
        outLedRGB_B.low();
    }

    @Override
    public void enableLedCapteurCouleur() {
        log.info("Led blanche capteur couleur allumé");
        outCmdLedCapteurRGB.high();
    }

    @Override
    public void disableLedCapteurCouleur() {
        log.info("Led blanche capteur couleur eteinte");
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

    private void enableElectroVanne() {
        log.info("Activation electrovanne");
        outElectroVanne.low();
    }

    private void disableElectroVanne() {
        log.info("Desactivation electrovanne");
        outElectroVanne.high();
    }

    @Override
    public void enablePompeAVide() {
        disableElectroVanne();
        log.info("Activation pompe a vide");
        outPompeAVide.high();
    }

    @Override
    public void disablePompeAVide() {
        log.info("Desactivation pompe a vide");
        outPompeAVide.low();
        enableElectroVanne();
        sleep(100);
        disableElectroVanne();
    }

    // ----------------------------------------------------------- //
    // -------------------------- BUSINESS ----------------------- //
    // ----------------------------------------------------------- //

    @Override
    public boolean glissiereOuverte() {
        boolean result = finCourseGlissiereDroite() && !finCourseGlissiereGauche();
        log.info("Glissière ouverte : {}", result);
        return result;
    }

    @Override
    public boolean glissiereFerme() {
        boolean result = !finCourseGlissiereDroite() && finCourseGlissiereGauche();
        log.info("Glissière fermé : {}", result);
        return result;
    }

    private void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
        }
    }
}
