package org.arig.robot.web.controller;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.services.INerellIOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestController
public class CapteursController extends AbstractCapteursController {

    @Autowired
    private NerellRobotStatus rs;

    @Autowired
    private INerellIOService ioService;

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
        super.afterPropertiesSet();

        numeriqueInfos.put("Bordure arrière droite", ioService::calageBordureDroit);
        numeriqueInfos.put("Bordure arrière gauche", ioService::calageBordureGauche);
        numeriqueInfos.put("Presence ventouse 1", ioService::presenceVentouse1);
        numeriqueInfos.put("Presence ventouse 2", ioService::presenceVentouse2);
        numeriqueInfos.put("Presence ventouse 3", ioService::presenceVentouse3);
        numeriqueInfos.put("Presence ventouse 4", ioService::presenceVentouse4);
        numeriqueInfos.put("Presence pince avant sup 1", ioService::presencePinceAvantSup1);
        numeriqueInfos.put("Presence pince avant sup 2", ioService::presencePinceAvantSup2);
        numeriqueInfos.put("Presence pince avant sup 3", ioService::presencePinceAvantSup3);
        numeriqueInfos.put("Presence pince avant sup 4", ioService::presencePinceAvantSup4);
        numeriqueInfos.put("Presence pince arrière 1", ioService::presencePinceArriere1);
        numeriqueInfos.put("Presence pince arrière 2", ioService::presencePinceArriere2);
        numeriqueInfos.put("Presence pince arrière 3", ioService::presencePinceArriere3);
        numeriqueInfos.put("Presence pince arrière 4", ioService::presencePinceArriere4);
        numeriqueInfos.put("Presence pince arrière 5", ioService::presencePinceArriere5);

        textInfos.put("Equipe", () -> rs.team().name());
    }
}
