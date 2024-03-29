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
        numeriqueInfos.put("Pince avant gauche", ioService::pinceAvantGauche);
        numeriqueInfos.put("Pince avant centre", ioService::pinceAvantCentre);
        numeriqueInfos.put("Pince avant droite", ioService::pinceAvantDroite);
        numeriqueInfos.put("Pince arriere gauche", ioService::pinceArriereGauche);
        numeriqueInfos.put("Pince arriere centre", ioService::pinceArriereCentre);
        numeriqueInfos.put("Pince arriere droite", ioService::pinceArriereDroite);
        numeriqueInfos.put("Présence avant gauche", ioService::presenceAvantGauche);
        numeriqueInfos.put("Présence avant centre", ioService::presenceAvantCentre);
        numeriqueInfos.put("Présence avant droite", ioService::presenceAvantDroite);
        numeriqueInfos.put("Présence arriere gauche", ioService::presenceArriereGauche);
        numeriqueInfos.put("Présence arriere centre", ioService::presenceArriereCentre);
        numeriqueInfos.put("Présence arriere droite", ioService::presenceArriereDroite);

        textInfos.put("Equipe", () -> (robotStatus.team() != null) ? robotStatus.team().name() : "???");
    }
}
