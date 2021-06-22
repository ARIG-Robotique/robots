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

        numeriqueInfos.put("Bordure arrière droite", ioService::calageBordureDroit);
        numeriqueInfos.put("Bordure arrière gauche", ioService::calageBordureGauche);

        textInfos.put("Equipe", () -> rs.team().name());
    }
}
