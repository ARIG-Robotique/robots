package org.arig.robot.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.OdinRobotStatus;
import org.arig.robot.services.IOdinIOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class OdinCapteursController extends AbstractCapteursController {

    @Autowired
    private OdinRobotStatus rs;

    @Autowired
    private IOdinIOService ioService;

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();

        numeriqueInfos.put("Presence avant droit", ioService::presenceAvantDroit);
        numeriqueInfos.put("Presence avant gauche", ioService::presenceAvantGauche);
        numeriqueInfos.put("Presence arriere droit", ioService::presenceArriereDroit);
        numeriqueInfos.put("Presence arriere gauche", ioService::presenceArriereGauche);
        numeriqueInfos.put("Presence ventouse avant droit", ioService::presenceVentouseAvantDroit);
        numeriqueInfos.put("Presence ventouse avant gauche", ioService::presenceVentouseAvantGauche);
        numeriqueInfos.put("Presence ventouse arriere droit", ioService::presenceVentouseArriereDroit);
        numeriqueInfos.put("Presence ventouse arriere gauche", ioService::presenceVentouseArriereGauche);
        numeriqueInfos.put("Bordure arrière droite", ioService::calageBordureArriereDroit);
        numeriqueInfos.put("Bordure arrière gauche", ioService::calageBordureArriereGauche);

        textInfos.put("Equipe", () -> rs.team().name());
    }
}
