package org.arig.robot.web.controller;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.services.IIOService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestController
public class CapteursController extends AbstractCapteursController implements InitializingBean {

    @Autowired
    private NerellRobotStatus rs;

    @Autowired
    private IIOService ioService;

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
        numeriqueInfos.put("Arret d'urgence", ioService::auOk);
        numeriqueInfos.put("Alim. Puissance 5V", ioService::alimPuissance5VOk);
        numeriqueInfos.put("Alim. Puissance 12V", ioService::alimPuissance12VOk);
        numeriqueInfos.put("Bordure arrière droite", ioService::calageBordureDroit);
        numeriqueInfos.put("Bordure arrière gauche", ioService::calageBordureGauche);
        numeriqueInfos.put("Presence pince avant lat 1", ioService::presencePinceAvantLat1);
        numeriqueInfos.put("Presence pince avant lat 2", ioService::presencePinceAvantLat2);
        numeriqueInfos.put("Presence pince avant lat 3", ioService::presencePinceAvantLat3);
        numeriqueInfos.put("Presence pince avant lat 4", ioService::presencePinceAvantLat4);
        numeriqueInfos.put("Presence pince avant sup 1", ioService::presencePinceAvantSup1);
        numeriqueInfos.put("Presence pince avant sup 2", ioService::presencePinceAvantSup2);
        numeriqueInfos.put("Presence pince avant sup 3", ioService::presencePinceAvantSup3);
        numeriqueInfos.put("Presence pince avant sup 4", ioService::presencePinceAvantSup4);
        numeriqueInfos.put("Presence pince arrière 1", ioService::presencePinceArriere1);
        numeriqueInfos.put("Presence pince arrière 2", ioService::presencePinceArriere2);
        numeriqueInfos.put("Presence pince arrière 3", ioService::presencePinceArriere3);
        numeriqueInfos.put("Presence pince arrière 4", ioService::presencePinceArriere4);
        numeriqueInfos.put("Presence pince arrière 5", ioService::presencePinceArriere5);
        numeriqueInfos.put("Tirette", ioService::tirette);

        // Capteurs informations analogique
        // NOP en 2020

        // Capteurs informations Text
        textInfos.put("Equipe", () -> rs.getTeam().name());
    }
}
