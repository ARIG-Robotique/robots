package org.arig.robot.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.services.INerellIOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class NerellCapteursController extends AbstractCapteursController {

    @Autowired
    private NerellRobotStatus rs;

    @Autowired
    private INerellIOService ioService;

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();

        numeriqueInfos.put("Bordure arrière droite", ioService::calageBordureDroit);
        numeriqueInfos.put("Bordure arrière gauche", ioService::calageBordureGauche);
        numeriqueInfos.put("Presence pince arrière 1", ioService::presencePinceArriere1);
        numeriqueInfos.put("Presence pince arrière 2", ioService::presencePinceArriere2);
        numeriqueInfos.put("Presence pince arrière 3", ioService::presencePinceArriere3);
        numeriqueInfos.put("Presence pince arrière 4", ioService::presencePinceArriere4);
        numeriqueInfos.put("Presence pince arrière 5", ioService::presencePinceArriere5);

        textInfos.put("Equipe", () -> rs.team().name());
    }
}
