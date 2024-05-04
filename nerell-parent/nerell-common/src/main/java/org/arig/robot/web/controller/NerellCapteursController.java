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
        numeriqueInfos.put("Inductif gauche", ioService::inductifGauche);
        numeriqueInfos.put("Inductif droit", ioService::inductifDroit);
        numeriqueInfos.put("Pince avant gauche", () -> ioService.pinceAvantGauche(false));
        numeriqueInfos.put("Pince avant centre", () -> ioService.pinceAvantCentre(false));
        numeriqueInfos.put("Pince avant droite", () -> ioService.pinceAvantDroite(false));
        numeriqueInfos.put("Pince arriere gauche", () -> ioService.pinceArriereGauche(false));
        numeriqueInfos.put("Pince arriere centre", () -> ioService.pinceArriereCentre(false));
        numeriqueInfos.put("Pince arriere droite", () -> ioService.pinceArriereDroite(false));
        numeriqueInfos.put("Présence avant gauche", () -> ioService.presenceAvantGauche(false));
        numeriqueInfos.put("Présence avant centre", () -> ioService.presenceAvantCentre(false));
        numeriqueInfos.put("Présence avant droite", () -> ioService.presenceAvantDroite(false));
        numeriqueInfos.put("Présence arriere gauche", () -> ioService.presenceArriereGauche(false));
        numeriqueInfos.put("Présence arriere centre", () -> ioService.presenceArriereCentre(false));
        numeriqueInfos.put("Présence arriere droite", () -> ioService.presenceArriereDroite(false));

        textInfos.put("Equipe", () -> (robotStatus.team() != null) ? robotStatus.team().name() : "???");
    }
}
