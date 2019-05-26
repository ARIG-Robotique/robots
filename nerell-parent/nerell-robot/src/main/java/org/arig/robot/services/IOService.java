package org.arig.robot.services;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.i2c.I2CBus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.arig.pi4j.gpio.extension.pcf.PCF8574GpioProvider;
import org.arig.pi4j.gpio.extension.pcf.PCF8574Pin;
import org.arig.robot.constants.IConstantesI2C;
import org.arig.robot.constants.IConstantesAnalogToDigital;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.constants.IConstantesUtiles;
import org.arig.robot.exception.I2CException;
import org.arig.robot.model.EStrategy;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.Team;
import org.arig.robot.model.enums.CouleurPalet;
import org.arig.robot.system.capteurs.TCS34725ColorSensor;
import org.arig.robot.system.capteurs.TCS34725ColorSensor.ColorData;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private TCS34725ColorSensor colorSensor;

//    @Autowired
//    private TinyLidar stockMagasinGauche;
//
//    @Autowired
//    private TinyLidar stockMagasinDroit;

    // Controlleur GPIO
    private GpioController gpio;
    private PCF8574GpioProvider pcfAlim;
    private PCF8574GpioProvider pcf1;
    private PCF8574GpioProvider pcf2;
    private PCF8574GpioProvider pcf3;

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
    private GpioPinDigitalInput inPresencePaletDroit;
    private GpioPinDigitalInput inPresencePaletGauche;
    private GpioPinDigitalInput inButeePaletDroit;
    private GpioPinDigitalInput inButeePaletGauche;
    private GpioPinDigitalInput inPresencePaletVentouseDroit;
    private GpioPinDigitalInput inPresencePaletVentouseGauche;
    private GpioPinDigitalInput inCalageBordureDroit;
    private GpioPinDigitalInput inCalageBordureGauche;
    private GpioPinDigitalInput inIndexCarousel;
    private GpioPinDigitalInput inPresenceLectureCouleur;

    // Référence sur les PIN Output
    // ----------------------------

    // GPIO
    private GpioPinDigitalOutput outCmdLedCapteurRGB;

    // PCF
    private GpioPinDigitalOutput outAlimPuissance5V;
    private GpioPinDigitalOutput outAlimPuissance12V;
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
//        inEquipe = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02);
//        inIrqAlim = gpio.provisionDigitalInputPin(RaspiPin.GPIO_07);
//        inIrqPcf1 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_04);
//        inIrq1 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_00);
//        inIrq3 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_01);
//        inIrq4 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_16);
//        inIrq5 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_15);
//        inIrq6 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_06);

        // Output
        outCmdLedCapteurRGB = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_05, PinState.LOW);

        // Config PCF8574 //
        // -------------- //
        // TODO Config IRQ
        pcfAlim = new PCF8574GpioProvider(bus, IConstantesI2C.PCF_ALIM_ADDRESS, true);
        pcf1 = new PCF8574GpioProvider(bus, IConstantesI2C.PCF1_ADDRESS, true);
        pcf2 = new PCF8574GpioProvider(bus, IConstantesI2C.PCF2_ADDRESS, true);
        pcf3 = new PCF8574GpioProvider(bus, IConstantesI2C.PCF3_ADDRESS, true);

        // Alim
        inAu = gpio.provisionDigitalInputPin(pcfAlim, PCF8574Pin.GPIO_04);
        inAlimPuissance5V = gpio.provisionDigitalInputPin(pcfAlim, PCF8574Pin.GPIO_05);
        inAlimPuissance12V = gpio.provisionDigitalInputPin(pcfAlim, PCF8574Pin.GPIO_06);

        outAlimPuissance5V = gpio.provisionDigitalOutputPin(pcfAlim, PCF8574Pin.GPIO_00);
        outAlimPuissance12V = gpio.provisionDigitalOutputPin(pcfAlim, PCF8574Pin.GPIO_01);

        // PCF1
        inTirette = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_00);
        inPresenceLectureCouleur = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_02);
        inPresencePaletDroit = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_03);
        inCalageBordureDroit = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_05);
        inPresencePaletGauche = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_06);
        inPresencePaletVentouseDroit = gpio.provisionDigitalInputPin(pcf1, PCF8574Pin.GPIO_07);

        // PCF2
        inIndexCarousel = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_00);
        inButeePaletGauche = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_03);
        inPresencePaletVentouseGauche = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_05);
        inButeePaletDroit = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_06);
        inCalageBordureGauche = gpio.provisionDigitalInputPin(pcf2, PCF8574Pin.GPIO_07);

        // PCF3
        outElectroVanneDroit = gpio.provisionDigitalOutputPin(pcf3, PCF8574Pin.GPIO_01);
        outPompeAVideDroite = gpio.provisionDigitalOutputPin(pcf3, PCF8574Pin.GPIO_04);
        outElectroVanneGauche = gpio.provisionDigitalOutputPin(pcf3, PCF8574Pin.GPIO_00);
        outPompeAVideGauche = gpio.provisionDigitalOutputPin(pcf3, PCF8574Pin.GPIO_03);

        // Etat initial des IOs
        disableLedCapteurCouleur();
        videElectroVanneDroite();
        videElectroVanneGauche();
        disablePompeAVideDroite();
        disablePompeAVideGauche();
    }

    @Override
    public void refreshAllPcf() {
        try {
            pcf1.readAll();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        try {
            pcf2.readAll();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        try {
            pcfAlim.readAll();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    // --------------------------------------------------------- //
    // --------------------- INFOS TECHNIQUE ------------------- //
    // --------------------------------------------------------- //

    @Override
    public Team equipe() {
        rs.setTeam(Team.valueOf(System.getProperty(IConstantesUtiles.ENV_PROP_TEAM)));
        return rs.getTeam();
    }

    @Override
    public List<EStrategy> strategies() {
        List<EStrategy> strategies = Stream.of(System.getProperty(IConstantesUtiles.ENV_PROP_STRATEGIES, "").split(","))
                .filter(StringUtils::isNotBlank)
                .map(EStrategy::valueOf)
                .collect(Collectors.toList());

        rs.setStrategies(strategies);

        return strategies;
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
        return inPresencePaletDroit.isLow();
    }

    @Override
    public boolean presencePaletGauche() {
        return inPresencePaletGauche.isLow();
    }

    @Override
    public boolean buteePaletDroit() {
        return inButeePaletDroit.isLow();
    }

    @Override
    public boolean buteePaletGauche() {
        return inButeePaletGauche.isLow();
    }

    @Override
    public boolean presencePaletVentouseDroit() {
        return inPresencePaletVentouseDroit.isLow();
    }

    @Override
    public boolean presencePaletVentouseGauche() {
        return inPresencePaletVentouseGauche.isLow();
    }

    @Override
    public boolean calageBordureArriereDroit() {
        return inCalageBordureDroit.isLow();
    }

    @Override
    public boolean calageBordureArriereGauche() {
        return inCalageBordureGauche.isLow();
    }

    @Override
    public boolean indexCarousel() {
        return inIndexCarousel.isLow();
    }

    @Override
    public boolean presenceLectureCouleur() {
        return inPresenceLectureCouleur.isLow();
    }

    // Analogique
    @Override
    public boolean paletPrisDansVentouseDroit() {
        boolean result = true;
/*
        try {
            int analogValue = i2cAdc.readCapteurValue(IConstantesAnalogToDigital.VACUOSTAT_DROIT);
            result = analogValue > IConstantesAnalogToDigital.VACUOSTAT_DROIT_SEUIL;
            log.info("Lecture capteur de vide droit {}", analogValue);
        } catch (I2CException e) {
            result = false;
        }
 */
        log.info("Présence module dans bras : {}", result);
        return result;
    }

    @Override
    public boolean paletPrisDansVentouseGauche() {
        boolean result = true;
/*
        try {
            int analogValue = i2cAdc.readCapteurValue(IConstantesAnalogToDigital.VACUOSTAT_GAUCHE);
            result = analogValue > IConstantesAnalogToDigital.VACUOSTAT_GAUCHE_SEUIL;
            log.info("Lecture capteur de vide gauche {}", analogValue);
        } catch (I2CException e) {
            result = false;
        }
*/
        log.info("Présence module dans bras : {}", result);
        return result;
    }

    @Override
    public byte nbPaletDansMagasinDroit() {
        // TODO tinylidar
        return convertDistanceToNbPaletDansStock(IConstantesNerellConfig.diametrePaletMm /*stockMagasinDroit.readValue()*/);
    }

    @Override
    public byte nbPaletDansMagasinGauche() {
        // TODO tinylidar
        return convertDistanceToNbPaletDansStock(IConstantesNerellConfig.diametrePaletMm /*stockMagasinGauche.readValue()*/);
    }

    @Override
    public int distanceTelemetreAvantDroit() {
        // TODO tinylidar
        return 10000 + IConstantesNerellConfig.dstTinylidarAvant;
    }

    @Override
    public int distanceTelemetreAvantGauche() {
        // TODO tinylidar
        return 10000 + IConstantesNerellConfig.dstTinylidarAvant;
    }

    private byte convertDistanceToNbPaletDansStock(int distance) {
        for (byte nb = 3, c = 1; nb >= 0; nb--, c++) {
            if (distance < ((c * IConstantesNerellConfig.diametrePaletMm) + IConstantesNerellConfig.offsetDetectionPaletMagasin)) {
                return nb;
            }
        }
        return 0;
    }

    // Couleur
    @Override
    public ColorData couleurPaletRaw() {
        return colorSensor.getColorData();
    }

    @Override
    public CouleurPalet couleurPalet() {
        ColorData c = couleurPaletRaw();

        int delta = 42;

        CouleurPalet result;
        if (c.b() > c.r() + delta && c.b() > c.g() + delta) {
            result = CouleurPalet.BLEU;

        } else if (c.g() > c.r() + delta && c.g() > c.b() + delta) {
            result = CouleurPalet.VERT;

        } else if (c.r() > c.b() + delta && c.r() > c.g() + delta) {
            result = CouleurPalet.ROUGE;

        } else {
            result = CouleurPalet.INCONNU;
        }

        log.info("Lecture capteur couleur R: {}, G: {}, B: {}, Palet: {}", c.r(), c.g(), c.b(), result.name());
        return result;
    }

    // --------------------------------------------------------- //
    // -------------------------- OUTPUT ----------------------- //
    // --------------------------------------------------------- //

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

    @Override
    public void airElectroVanneDroite() {
        log.info("Air electrovanne droite");
        outElectroVanneDroit.low();
    }

    @Override
    public void videElectroVanneDroite() {
        log.info("Vide electrovanne droite");
        outElectroVanneDroit.high();
    }

    @Override
    public void airElectroVanneGauche() {
        log.info("Air electrovanne gauche");
        outElectroVanneGauche.low();
    }

    @Override
    public void videElectroVanneGauche() {
        log.info("Vide electrovanne gauche");
        outElectroVanneGauche.high();
    }

    @Override
    public void enablePompeAVideDroite() {
        log.info("Activation pompe a vide droite");
        outPompeAVideDroite.high();
    }

    @Override
    public void disablePompeAVideDroite() {
        log.info("Desactivation pompe a vide droite");
        outPompeAVideDroite.low();
    }

    @Override
    public void enablePompeAVideGauche() {
        log.info("Activation pompe a vide gauche");
        outPompeAVideGauche.high();
    }

    @Override
    public void disablePompeAVideGauche() {
        log.info("Desactivation pompe a vide gauche");
        outPompeAVideGauche.low();
    }
}
