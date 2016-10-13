package org.arig.robot.web.controller;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesGPIO;
import org.arig.robot.exception.I2CException;
import org.arig.robot.services.IOService;
import org.arig.robot.system.capteurs.I2CAdcAnalogInput;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author gdepuille on 21/12/13.
 */
@Slf4j
@RestController
public class CapteursController extends AbstractCapteursController implements InitializingBean {

    @Autowired
    private IOService ioService;

    @Autowired
    private I2CAdcAnalogInput i2CAdcAnalogInput;

    @Getter
    @Accessors(fluent = true)
    private final Map<String, BooleanValue> numeriqueInfos = new LinkedHashMap<>();
    
    @Getter
    @Accessors(fluent = true)
    private final Map<String, DoubleValue> analogiqueInfos = new LinkedHashMap<>();
    
    @Getter
    @Accessors(fluent = true)
    private final Map<String, StringValue> textInfos = new LinkedHashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        // Capteurs informations numérique
        numeriqueInfos.put("AU", ioService::auOk);
        numeriqueInfos.put("Puissance Moteur", ioService::alimMoteurOk);
        numeriqueInfos.put("Puissance Servo", ioService::alimServoOk);
        numeriqueInfos.put("Tirette", ioService::tirette);
        numeriqueInfos.put("Bouton tapis", ioService::btnTapis);
        numeriqueInfos.put("Butée avant gauche", ioService::buteeAvantGauche);
        numeriqueInfos.put("Butée avant droite", ioService::buteeAvantDroit);
        numeriqueInfos.put("Butée arrière gauche", ioService::buteeArriereGauche);
        numeriqueInfos.put("Butée arrière droite", ioService::buteeArriereDroit);
        numeriqueInfos.put("Pied ascenseur", ioService::piedCentre);
        numeriqueInfos.put("Pied gauche", ioService::piedGauche);
        numeriqueInfos.put("Pied droit", ioService::piedDroit);
        numeriqueInfos.put("Produit gauche", ioService::produitGauche);
        numeriqueInfos.put("Produit droit", ioService::produitDroit);
        numeriqueInfos.put("Gobelet gauche", ioService::gobeletGauche);
        numeriqueInfos.put("Gobelet droit", ioService::gobeletDroit);

        // Capteurs informations analogique
        analogiqueInfos.put("GP2D lateral avant Gauche", () -> readI2CAnalogValue(IConstantesGPIO.GP2D_AVANT_LATERAL_GAUCHE));
        analogiqueInfos.put("GP2D avant Gauche", () -> readI2CAnalogValue(IConstantesGPIO.GP2D_AVANT_GAUCHE));
        analogiqueInfos.put("GP2D avant Droit", () -> readI2CAnalogValue(IConstantesGPIO.GP2D_AVANT_DROIT));
        analogiqueInfos.put("GP2D lateral avant Droit", () -> readI2CAnalogValue(IConstantesGPIO.GP2D_AVANT_LATERAL_DROIT));
        
        // Capteurs informations Text
        textInfos.put("Equipe", () -> ioService.equipe().name());
    }

    private Double readI2CAnalogValue(byte capteurId) {
        try {
            return (double) i2CAdcAnalogInput.readCapteurValue(capteurId);
        } catch (I2CException e) {
            log.warn("Erreur de lecture du capteur {} pour le monitoring", capteurId);
            return (double) -1;
        }
    }
}
