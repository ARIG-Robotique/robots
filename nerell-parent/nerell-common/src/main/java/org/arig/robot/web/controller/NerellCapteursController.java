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
    private NerellRobotStatus robotStatus;

    @Autowired
    private INerellIOService ioService;

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();

        numeriqueInfos.put("Bordure arrière droite", ioService::calageBordureArriereDroit);
        numeriqueInfos.put("Bordure arrière gauche", ioService::calageBordureArriereGauche);
        numeriqueInfos.put("Bordure avant droite", ioService::calageBordureAvantDroit);
        numeriqueInfos.put("Bordure avant gauche", ioService::calageBordureAvantGauche);
        //numeriqueInfos.put("Bordure custom droite", ioService::calageBordureCustomDroit);
        //numeriqueInfos.put("Bordure custom gauche", ioService::calageBordureCustomGauche);

        numeriqueInfos.put("Presence carre de fouille", ioService::presenceCarreFouille);
        numeriqueInfos.put("Presence prise bras", ioService::presencePriseBras);
        numeriqueInfos.put("Presence pince stock 1", ioService::presenceStock1);
        numeriqueInfos.put("Presence pince stock 2", ioService::presenceStock2);
        numeriqueInfos.put("Presence pince stock 3", ioService::presenceStock3);
        numeriqueInfos.put("Presence pince stock 4", ioService::presenceStock4);
        numeriqueInfos.put("Presence pince stock 5", ioService::presenceStock5);
        numeriqueInfos.put("Presence pince stock 6", ioService::presenceStock6);
        numeriqueInfos.put("Presence ventouse bas", ioService::presenceVentouseBas);
        numeriqueInfos.put("Presence ventouse haut", ioService::presenceVentouseHaut);

        textInfos.put("Equipe", () -> (robotStatus.team() != null) ? robotStatus.team().name() : "???");
    }
}
