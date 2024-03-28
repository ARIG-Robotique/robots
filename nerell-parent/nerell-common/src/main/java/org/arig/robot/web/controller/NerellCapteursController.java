package org.arig.robot.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.CouleurEchantillon;
import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.services.NerellIOService;
import org.arig.robot.system.capteurs.TCS34725ColorSensor;
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

        numeriqueInfos.put("In 1 : 1", ioService::in1_1);
        numeriqueInfos.put("In 1 : 2", ioService::in1_2);
        numeriqueInfos.put("In 1 : 3", ioService::in1_3);
        numeriqueInfos.put("In 1 : 4", ioService::in1_4);
        numeriqueInfos.put("In 1 : 5", ioService::in1_5);
        numeriqueInfos.put("In 1 : 6", ioService::in1_6);
        numeriqueInfos.put("In 1 : 7", ioService::in1_7);
        numeriqueInfos.put("In 1 : 8", ioService::in1_8);
        numeriqueInfos.put("In 2 : 1", ioService::in2_1);
        numeriqueInfos.put("In 2 : 2", ioService::in2_2);
        numeriqueInfos.put("In 2 : 3", ioService::in2_3);
        numeriqueInfos.put("In 2 : 4", ioService::in2_4);
        numeriqueInfos.put("In 2 : 5", ioService::in2_5);
        numeriqueInfos.put("In 2 : 6", ioService::in2_6);
        numeriqueInfos.put("In 2 : 7", ioService::in2_7);
        numeriqueInfos.put("In 2 : 8", ioService::in2_8);
        numeriqueInfos.put("In 3 : 1", ioService::in3_1);
        numeriqueInfos.put("In 3 : 2", ioService::in3_2);
        numeriqueInfos.put("In 3 : 3", ioService::in3_3);
        numeriqueInfos.put("In 3 : 4", ioService::in3_4);
        numeriqueInfos.put("In 3 : 5", ioService::in3_5);
        numeriqueInfos.put("In 3 : 6", ioService::in3_6);
        numeriqueInfos.put("In 3 : 7", ioService::in3_7);
        numeriqueInfos.put("In 3 : 8", ioService::in3_8);

        textInfos.put("Equipe", () -> (robotStatus.team() != null) ? robotStatus.team().name() : "???");
    }
}
