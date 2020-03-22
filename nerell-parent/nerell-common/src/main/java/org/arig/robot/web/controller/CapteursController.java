package org.arig.robot.web.controller;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.RobotStatus;
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
    private RobotStatus rs;

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
        numeriqueInfos.put("AU", ioService::auOk);
        numeriqueInfos.put("Puissance 5V", ioService::alimPuissance5VOk);
        numeriqueInfos.put("Puissance 12V", ioService::alimPuissance12VOk);
        numeriqueInfos.put("Tirette", ioService::tirette);
        numeriqueInfos.put("Bordure arrière droite", ioService::calageBordureArriereDroit);
        numeriqueInfos.put("Bordure arrière gauche", ioService::calageBordureArriereGauche);
        numeriqueInfos.put("Presence pince avant 1", ioService::presencePinceAvant1);
        numeriqueInfos.put("Presence pince avant 2", ioService::presencePinceAvant2);
        numeriqueInfos.put("Presence pince avant 3", ioService::presencePinceAvant3);
        numeriqueInfos.put("Presence pince avant 4", ioService::presencePinceAvant4);
        numeriqueInfos.put("Presence pince arrière 1", ioService::presencePinceArriere1);
        numeriqueInfos.put("Presence pince arrière 2", ioService::presencePinceArriere2);
        numeriqueInfos.put("Presence pince arrière 3", ioService::presencePinceArriere3);
        numeriqueInfos.put("Presence pince arrière 4", ioService::presencePinceArriere4);
        numeriqueInfos.put("Presence pince arrière 5", ioService::presencePinceArriere5);

        // Capteurs informations analogique

        // Capteurs informations Text
        textInfos.put("Equipe", () -> rs.getTeam().name());
    }
}
