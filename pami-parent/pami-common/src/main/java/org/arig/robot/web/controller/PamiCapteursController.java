package org.arig.robot.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.PamiRobotStatus;
import org.arig.robot.services.PamiIOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class PamiCapteursController extends AbstractCapteursController {

    @Autowired
    private PamiRobotStatus robotStatus;

    @Autowired
    private PamiIOService ioService;

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();

        textInfos.put("Equipe", () -> (robotStatus.team() != null) ? robotStatus.team().name() : "???");
    }
}
