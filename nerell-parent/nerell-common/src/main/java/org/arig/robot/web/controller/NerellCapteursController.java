package org.arig.robot.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.services.NerellIOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class NerellCapteursController extends AbstractCapteursController {

    @Autowired
    private NerellRobotStatus robotStatus;

    @Autowired
    private NerellIOService ioService;

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();

        numeriqueInfos.put("Tirette", ioService::tirette);

        numeriqueInfos.put("Calage avant gauche", ioService::calageAvantGauche);
        numeriqueInfos.put("Calage avant droit", ioService::calageAvantDroit);
        numeriqueInfos.put("Calage arriere gauche", ioService::calageArriereGauche);
        numeriqueInfos.put("Calage arriere droit", ioService::calageArriereDroit);

        numeriqueInfos.put("Stock avant gauche", () -> ioService.solAvantGauche(false));
        numeriqueInfos.put("Stock avant droite", () -> ioService.solAvantDroite(false));
        numeriqueInfos.put("Stock arriere gauche", () -> ioService.solArriereGauche(false));
        numeriqueInfos.put("Stock arriere droite", () -> ioService.solArriereDroite(false));

        numeriqueInfos.put("Pince avant gauche", () -> ioService.pinceAvantGauche(false));
        numeriqueInfos.put("Pince avant droite", () -> ioService.pinceAvantDroite(false));
        numeriqueInfos.put("Pince arriere gauche", () -> ioService.pinceArriereGauche(false));
        numeriqueInfos.put("Pince arriere droite", () -> ioService.pinceArriereDroite(false));

        numeriqueInfos.put("Tiroir avant haut", () -> ioService.tiroirAvantHaut(false));
        numeriqueInfos.put("Tiroir avant bas", () -> ioService.tiroirAvantBas(false));
        numeriqueInfos.put("Tiroir arriere haut", () -> ioService.tiroirArriereHaut(false));
        numeriqueInfos.put("Tiroir arriere bas", () -> ioService.tiroirArriereBas(false));

        textInfos.put("Equipe", () -> (robotStatus.team() != null) ? robotStatus.team().name() : "???");
    }
}
