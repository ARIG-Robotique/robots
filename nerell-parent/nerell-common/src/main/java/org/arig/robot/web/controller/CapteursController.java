package org.arig.robot.web.controller;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesI2CAdc;
import org.arig.robot.exception.I2CException;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.services.IIOService;
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
    private RobotStatus rs;

    @Autowired
    private IIOService ioService;

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
        numeriqueInfos.put("Puissance 5V", ioService::alimPuissance5VOk);
        numeriqueInfos.put("Puissance 8V", ioService::alimPuissance8VOk);
        numeriqueInfos.put("Puissance 12V", ioService::alimPuissance12VOk);
        numeriqueInfos.put("Tirette", ioService::tirette);
        numeriqueInfos.put("Bordure avant", ioService::bordureAvant);
        numeriqueInfos.put("Bordure arrière droite", ioService::bordureArriereDroite);
        numeriqueInfos.put("Bordure arrière gauche", ioService::bordureArriereGauche);
        numeriqueInfos.put("Presence entrée magasin", ioService::presenceEntreeMagasin);
        numeriqueInfos.put("Presence dévidoir", ioService::presenceDevidoir);
        numeriqueInfos.put("Présence rouleaux", ioService::presenceRouleaux);
        numeriqueInfos.put("Présence pince droite", ioService::presencePinceDroite);
        numeriqueInfos.put("Présence pince centre", ioService::presencePinceCentre);
        numeriqueInfos.put("Présence fusée", ioService::presenceFusee);
        numeriqueInfos.put("Présence balles aspiration", ioService::presenceBallesAspiration);
        numeriqueInfos.put("Présence base lunaire droite", ioService::presenceBaseLunaireDroite);
        numeriqueInfos.put("Présence base lunaire gauche", ioService::presenceBaseLunaireGauche);
        numeriqueInfos.put("Fin course glissière droite", ioService::finCourseGlissiereDroite);
        numeriqueInfos.put("Fin course glissière gauche", ioService::finCourseGlissiereGauche);

        // Capteurs informations analogique
        analogiqueInfos.put("GP2D avant Gauche", () -> readI2CAnalogValue(IConstantesI2CAdc.GP2D_AVANT_GAUCHE));
        analogiqueInfos.put("GP2D avant Centre", () -> readI2CAnalogValue(IConstantesI2CAdc.GP2D_AVANT_CENTRE));
        analogiqueInfos.put("GP2D avant Droit", () -> readI2CAnalogValue(IConstantesI2CAdc.GP2D_AVANT_DROIT));
        analogiqueInfos.put("GP2D Scan haut", () -> readI2CAnalogValue(IConstantesI2CAdc.GP2D_SCAN_HAUT));
        analogiqueInfos.put("GP2D Scan bas", () -> readI2CAnalogValue(IConstantesI2CAdc.GP2D_SCAN_BAS));

        // Capteurs informations Text
        textInfos.put("Equipe", () -> rs.getTeam().name());
        textInfos.put("Front color hex", () -> ioService.frontColor().hexColor());
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
