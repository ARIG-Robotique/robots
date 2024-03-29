package org.arig.robot.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.OdinRobotStatus;
import org.arig.robot.services.OdinIOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class OdinCapteursController extends AbstractCapteursController {

    @Autowired
    private OdinRobotStatus robotStatus;

    @Autowired
    private OdinIOService ioService;

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();

        textInfos.put("Equipe", () -> (robotStatus.team() != null) ? robotStatus.team().name() : "???");
    }
}
