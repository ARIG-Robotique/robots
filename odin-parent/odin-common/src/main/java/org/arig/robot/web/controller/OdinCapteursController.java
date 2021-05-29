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
        numeriqueInfos.put("Presence ventouse avant gauche", ioService::presenceVentouseAvantGauche);
        numeriqueInfos.put("Presence ventouse avant droit", ioService::presenceVentouseAvantDroit);
        numeriqueInfos.put("Presence ventouse arriere gauche", ioService::presenceVentouseArriereGauche);
        numeriqueInfos.put("Presence ventouse arriere droit", ioService::presenceVentouseArriereDroit);

        textInfos.put("Equipe", () -> rs.team().name());
    }
}
