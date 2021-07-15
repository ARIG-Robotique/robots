package org.arig.robot.web.controller;

import org.arig.robot.constants.INerellConstantesServos;
import org.arig.robot.model.servos.ServoGroup;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class NerellServosController extends AbstractServosController {

    private static List<ServoGroup> servoConfigs = new ArrayList<>();

    @Override
    protected int[][] getBatchPositions(Byte idBatch, Byte position) {
        try {
            return INerellConstantesServos.BATCH_CONFIG.get(idBatch).get(position);
        } catch (Exception e) {
            return new int[0][];
        }
    }

    @Override
    protected List<ServoGroup> servosConfig() {
        servoConfigs.forEach(sc ->
            sc.getServos().forEach(s -> {
                s.setCurrentPosition(sd21Servos.getPosition(s.getId()));
                s.setCurrentSpeed(sd21Servos.getSpeed(s.getId()));
            })
        );

        return servoConfigs;
    }
}
