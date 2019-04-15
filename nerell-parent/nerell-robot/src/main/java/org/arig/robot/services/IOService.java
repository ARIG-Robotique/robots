package org.arig.robot.services;

import com.pi4j.gpio.extension.pcf.PCF8574GpioProvider;
import com.pi4j.gpio.extension.pcf.PCF8574Pin;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.i2c.I2CBus;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesI2C;
import org.arig.robot.constants.IConstantesI2CAdc;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.I2CException;
import org.arig.robot.model.Palet.Couleur;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.Team;
import org.arig.robot.model.monitor.MonitorTimeSerie;
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.arig.robot.system.capteurs.I2CAdcAnalogInput;
import org.arig.robot.system.capteurs.TCS34725ColorSensor;
import org.arig.robot.system.capteurs.TCS34725ColorSensor.ColorData;
import org.arig.robot.system.capteurs.TinyLidar;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
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
    private TCS34725ColorSensor colorSensor;

    @Autowired
    private TinyLidar stockMagasinGauche;

    @Autowired
    private TinyLidar stockMagasinDroit;

    // Controlleur GPIO
    private GpioController gpio;
    private PCF8574GpioProvider pcfAlim;
    private PCF8574GpioProvider pcf1;
    private PCF8574GpioProvider pcf2;
    private PCF8574GpioProvider pcf3;

    // Référence sur les PIN Input //

    // IRQ
    private GpioPinDigitalInput inIrqAlim;
    private GpioPinDigitalInput inIrqPcf1;
    private GpioPinDigitalInput inIrq1;
    private GpioPinDigitalInput inIrq3;
    private GpioPinDigitalInput inIrq4;
    private GpioPinDigitalInput inIrq5;
    private GpioPinDigitalInput inIrq6;

    // Technique
    private GpioPinDigitalInput inEquipe;
    private GpioPinDigitalInput inAu;
    private GpioPinDigitalInput inAlimPuissance5V;
    private GpioPinDigitalInput inAlimPuissance8V;
    private GpioPinDigitalInput inAlimPuissance12V;
    private GpioPinDigitalInput inTirette;

    // Input : Numerique
    private GpioPinDigitalInput inPresencePaletDroit;
    private GpioPinDigitalInput inPresencePaletGauche;
    private GpioPinDigitalInput inButeePaletDroit;
    private GpioPinDigitalInput inButeePaletGauche;
    private GpioPinDigitalInput inPresencePaletVentouseDroit;
    private GpioPinDigitalInput inPresencePaletVentouseGauche;
    private GpioPinDigitalInput inCalageBordureDroit;
    private GpioPinDigitalInput inCalageBordureGauche;
    private GpioPinDigitalInput inTrappeMagasinDroitFerme;
    private GpioPinDigitalInput inTrappeMagasinGaucheFerme;
    private GpioPinDigitalInput inIndexBarillet;
    private GpioPinDigitalInput inPresenceLectureCouleur;

    // Référence sur les PIN Output
    private GpioPinDigitalOutput outAlimPuissance5V;
    private GpioPinDigitalOutput outAlimPuissance8V;
    private GpioPinDigitalOutput outAlimPuissance12V;
    private GpioPinDigitalOutput outCmdLedCapteurRGB;
    private GpioPinDigitalOutput outLedRGB_R;
    private GpioPinDigitalOutput outLedRGB_G;
    private GpioPinDigitalOutput outLedRGB_B;
    private GpioPinDigitalOutput outElectroVanneDroit;
    private GpioPinDigitalOutput outElectroVanneGauche;
    private GpioPinDigitalOutput outPompeAVideDroite;
    private GpioPinDigitalOutput outPompeAVideGauche;

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
        inAu = gpio.provisionDigitalInputPin(pcfAlim, PCF8574Pin.GPIO_04);
        inAlimPuissance5V = gpio.provisionDigitalInputPin(pcfAlim, PCF8574Pin.GPIO_05);
        inAlimPuissance8V = gpio.provisionDigitalInputPin(pcfAlim, PCF8574Pin.GPIO_07);
        inAlimPuissance12V = gpio.provisionDigitalInputPin(pcfAlim, PCF8574Pin.GPIO_06);

        outAlimPuissance5V = gpio.provisionDigitalOutputPin(pcfAlim, PCF8574Pin.GPIO_00);
        outAlimPuissance8V = gpio.provisionDigitalOutputPin(pcfAlim, PCF8574Pin.GPIO_02);
        outAlimPuissance12V = gpio.provisionDigitalOutputPin(pcfAlim, PCF8574Pin.GPIO_01);

        // PCF1
        inTirette = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_00);
        inPresencePaletDroit = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_01);
        inPresencePaletGauche = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_02);
        inButeePaletDroit = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_03);
        inButeePaletGauche = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_04);
        inPresencePaletVentouseDroit = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_05);
        inPresencePaletVentouseGauche = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_06);
        inIndexBarillet = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_07);

        // PCF2
        inPresenceLectureCouleur = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_00);
        inTrappeMagasinDroitFerme = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_01);
        inTrappeMagasinGaucheFerme = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_02);
        inCalageBordureDroit = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_03);
        inCalageBordureGauche = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_04);

        // PCF3
        outElectroVanneDroit = gpio.provisionDigitalOutputPin(pcf3, PCF8574Pin.GPIO_00);
        outPompeAVideDroite = gpio.provisionDigitalOutputPin(pcf3, PCF8574Pin.GPIO_01);
        outElectroVanneGauche = gpio.provisionDigitalOutputPin(pcf3, PCF8574Pin.GPIO_02);
        outPompeAVideGauche = gpio.provisionDigitalOutputPin(pcf3, PCF8574Pin.GPIO_03);

        // Etat initial des IOs
        outAlimPuissance8V.high(); // Désactivé
        disableLedCapteurCouleur();
        clearColorLedRGB();
        disableElectroVanneDroite();
        disableElectroVanneGauche();
        disablePompeAVideDroite();
        disablePompeAVideGauche();
    }

    // --------------------------------------------------------- //
    // --------------------- INFOS TECHNIQUE ------------------- //
    // --------------------------------------------------------- //

    @Override
    public Team equipe() {
        rs.setTeam(inEquipe.isHigh() ? Team.VIOLET : Team.JAUNE);
        return rs.getTeam();
    }

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

    // --------------------------------------------------------- //
    // -------------------------- INPUT ------------------------ //
    // --------------------------------------------------------- //

    // Numerique

    @Override
    public boolean ledCapteurCouleur() {
        boolean result = outCmdLedCapteurRGB.isHigh();
        log.info("LED capteur couleur allumé : {}", result);
        return result;
    }

    @Override
    public boolean presencePaletDroit() {
        boolean result = inPresencePaletDroit.isLow();
        log.info("Presence pince centre : {}", result);
        return result;
    }

    @Override
    public boolean presencePaletGauche() {
        boolean result = inPresencePaletGauche.isLow();
        log.info("Presence pince droite : {}", result);
        return result;
    }

    @Override
    public boolean buteePaletDroit() {
        boolean result = inButeePaletDroit.isLow();
        log.info("Presence fusée : {}", result);
        return result;
    }

    @Override
    public boolean buteePaletGauche() {
        boolean result = inButeePaletGauche.isLow();
        log.info("Présence bordure avant : {}", result);
        return result;
    }

    @Override
    public boolean presencePaletVentouseDroit() {
        boolean result = inPresencePaletVentouseDroit.isLow();
        log.info("Présence comptage magasin : {}", result);
        return result;
    }

    @Override
    public boolean presencePaletVentouseGauche() {
        boolean result = inPresencePaletVentouseGauche.isLow();
        log.info("Présence dévidoir : {}", result);
        return result;
    }

    @Override
    public boolean calageBordureArriereDroit() {
        boolean result = inCalageBordureDroit.isLow();
        log.info("Présence rouleaux : {}", result);
        return result;
    }

    @Override
    public boolean calageBordureArriereGauche() {
        boolean result = inCalageBordureGauche.isLow();
        log.info("Présence rouleaux : {}", result);
        return result;
    }

    @Override
    public boolean trappeMagasinDroitFerme() {
        boolean result = inTrappeMagasinDroitFerme.isLow();
        log.info("Presence balle aspiration : {}", result);
        return result;
    }

    @Override
    public boolean trappeMagasinGaucheFerme() {
        boolean result = inTrappeMagasinGaucheFerme.isLow();
        log.info("Presence balle aspiration : {}", result);
        return result;
    }

    @Override
    public boolean indexBarillet() {
        boolean result = inIndexBarillet.isLow();
        log.info("Présence bordure arrière droite : {}", result);
        return result;
    }

    @Override
    public boolean presenceLectureCouleur() {
        boolean result = inPresenceLectureCouleur.isLow();
        log.info("Presence base lunaire droite : {}", result);
        return result;
    }

    // Analogique

    @Override
    public boolean paletPrisDansVentouseDroit() {
        boolean result;
        try {
            int analogValue = i2cAdc.readCapteurValue(IConstantesI2CAdc.VACUOSTAT_DROIT);
            result = analogValue > IConstantesI2CAdc.VACUOSTAT_DROIT_SEUIL;
            log.info("Lecture capteur de vide droit {}", analogValue);
        } catch (I2CException e) {
            result = false;
        }
        log.info("Présence module dans bras : {}", result);
        return result;
    }

    @Override
    public boolean paletPrisDansVentouseGauche() {
        boolean result;
        try {
            int analogValue = i2cAdc.readCapteurValue(IConstantesI2CAdc.VACUOSTAT_GAUCHE);
            result = analogValue > IConstantesI2CAdc.VACUOSTAT_GAUCHE_SEUIL;
            log.info("Lecture capteur de vide gauche {}", analogValue);
        } catch (I2CException e) {
            result = false;
        }
        log.info("Présence module dans bras : {}", result);
        return result;
    }

    @Override
    public byte nbPaletDansMagasinDroit() {
        return convertDistanceToNbPaletDansStock(stockMagasinDroit.readValue());
    }

    @Override
    public byte nbPaletDansMagasinGauche() {
        return convertDistanceToNbPaletDansStock(stockMagasinGauche.readValue());
    }

    private byte convertDistanceToNbPaletDansStock(int distance) {
        for (byte nb = 3, c = 1; nb >= 0 ; nb--, c++) {
            if (distance < ((c * IConstantesNerellConfig.diametrePaletMm) + IConstantesNerellConfig.offsetDetectionPaletMagasin)) {
                return nb;
            }
        }
        return 0;
    }

    @Override
    public int distanceTelemetreAvantDroit() {
        // TODO
        return 0;
    }

    @Override
    public int distanceTelemetreAvantGauche() {
        // TODO
        return 0;
    }

    // Couleur

    @Override
    public ColorData couleurPaletRaw() {
        return colorSensor.getColorData();
    }

    @Override
    public Couleur couleurPalet() {
        ColorData c = couleurPaletRaw();

        MonitorTimeSerie mts = new MonitorTimeSerie();
        mts.measurementName("couleur")
                .addTag(MonitorTimeSerie.TAG_NAME, "palet")
                .addField("r", c.r())
                .addField("g", c.g())
                .addField("b", c.b());
        monitoring.addTimeSeriePoint(mts);

        int delta = 42;

        Couleur result;
        if (c.b() > c.r() + delta && c.b() > c.g() + delta) {
            result = Couleur.BLEU;

        } else if (c.g() > c.r() + delta && c.g() > c.b() + delta) {
            result = Couleur.VERT;

        } else if (c.r() > c.b() + delta && c.r() > c.g() + delta) {
            result = Couleur.ROUGE;

        } else {
            result = Couleur.INCONNU;
        }

        log.info("Lecture capteur couleur R: {}, G: {}, B: {}, Palet: {}", c.r(), c.g(), c.b(), result.name());
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
        if (rs.getTeam() == Team.VIOLET) {
            log.info("Led RGB couleur Team VIOLET");
            outLedRGB_R.high();
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

    private void enableElectroVanneDroite() {
        log.info("Activation electrovanne droite");
        outElectroVanneDroit.low();
    }

    private void disableElectroVanneDroite() {
        log.info("Desactivation electrovanne droite");
        outElectroVanneDroit.high();
    }

    private void enableElectroVanneGauche() {
        log.info("Activation electrovanne gauche");
        outElectroVanneGauche.low();
    }

    private void disableElectroVanneGauche() {
        log.info("Desactivation electrovanne gauche");
        outElectroVanneGauche.high();
    }

    @Override
    public void enablePompeAVideDroite() {
        disableElectroVanneDroite();
        log.info("Activation pompe a vide droite");
        outPompeAVideDroite.high();
    }

    @Override
    public void disablePompeAVideDroite() {
        log.info("Desactivation pompe a vide droite");
        outPompeAVideDroite.low();
        enableElectroVanneDroite();
        sleep(100);
        disableElectroVanneDroite();
    }

    @Override
    public void enablePompeAVideGauche() {
        disableElectroVanneGauche();
        log.info("Activation pompe a vide gauche");
        outPompeAVideGauche.high();
    }

    @Override
    public void disablePompeAVideGauche() {
        log.info("Desactivation pompe a vide gauche");
        outPompeAVideGauche.low();
        enableElectroVanneGauche();
        sleep(100);
        disableElectroVanneGauche();
    }

    // ----------------------------------------------------------- //
    // -------------------------- BUSINESS ----------------------- //
    // ----------------------------------------------------------- //


    // ----------------------------------------------------------- //

    private void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
        }
    }
}
