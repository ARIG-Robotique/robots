package org.arig.robot.web.controller;

import org.arig.robot.constants.IConstantesServos;
import org.arig.robot.model.servos.ServoConfig;
import org.arig.robot.model.servos.ServoUtils;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.List;

@RestController
public class ServosController extends AbstractServosController {

    private static List<ServoConfig> servoConfigs = new LinkedList<>();

    static {
        servoConfigs.add(new ServoConfig()
                .setId(IConstantesServos.ASCENSEUR_AVANT)
                .setGroup(ServoUtils.groupImportant())
                .setName("Ascenseur avant")
                .position("Haut", IConstantesServos.POS_ASCENSEUR_AVANT_HAUT)
                .position("Bas", IConstantesServos.POS_ASCENSEUR_AVANT_BAS)
        );
    }

    @Override
    protected List<ServoConfig> servosConfig() {
        servoConfigs.forEach(sc -> {
            sc.setCurrentPosition(sd21Servos.getPosition(sc.getId()));
            sc.setCurrentSpeed(sd21Servos.getSpeed(sc.getId()));
        });

        return servoConfigs;
    }
}
