package org.arig.robot.web.controller;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.OdinRobotStatus;
import org.arig.robot.services.IOdinIOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestController
public class OdinCapteursController extends AbstractCapteursController {
    @Autowired
    private OdinRobotStatus rs;

    @Autowired
    private IOdinIOService ioService;

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
        // TODO TOR Ventouses Odin
        numeriqueInfos.put("Presence ventouse avant 1", ioService::presenceVentouseAvant1);
        numeriqueInfos.put("Presence ventouse avant 2", ioService::presenceVentouseAvant2);
        numeriqueInfos.put("Presence ventouse arriere 1", ioService::presenceVentouseArriere1);
        numeriqueInfos.put("Presence ventouse arriere 2", ioService::presenceVentouseArriere2);

        textInfos.put("Equipe", () -> rs.team().name());
    }
}
