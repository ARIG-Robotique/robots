package org.arig.robot.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesConfig;
import org.arig.robot.model.servos.ServoConfig;
import org.arig.robot.model.servos.ServoGroup;
import org.arig.robot.system.servos.SD21Servos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Slf4j
@RequestMapping("/servos")
@Profile(IConstantesConfig.profileMonitoring)
public abstract class AbstractServosController {

    @Autowired
    protected SD21Servos sd21Servos;

    protected abstract List<ServoGroup> servosConfig();

    protected abstract int[][] getBatchPositions(Byte idGroupe, Byte position);

    @GetMapping
    public final List<ServoGroup> config() {
        return servosConfig();
    }

    @GetMapping(value = "/{idServo}")
    public final ServoConfig getServoPositionAndSpeed(@PathVariable("idServo") final Byte idServo) {
        return servosConfig().stream()
                .map(ServoGroup::getServos)
                .flatMap(List::stream)
                .filter(s -> s.getId() == idServo)
                .findFirst()
                .orElse(null);
    }

    @PostMapping(value = "/{idServo}")
    public final void servoPositionAndSpeed(
            @PathVariable("idServo") final Byte idServo,
            @RequestParam("position") final Integer position,
            @RequestParam(value = "speed", required = false) final Byte speed) {

        if (speed != null) {
            log.info("Modification du servo moteur {} : Pos -> {} ; Speed -> {}", idServo, position, speed);
            sd21Servos.setPositionAndSpeed(idServo, position, speed);
        } else {
            log.info("Modification du servo moteur {} : Pos -> {}", idServo, position);
            sd21Servos.setPosition(idServo, position);
        }
    }

    @PostMapping({"/groupe/{idBatch}", "/batch/{idBatch}"}) // rétro-compat groupe -> batch
    public final void batchPosition(@PathVariable("idBatch") final Byte idBatch,
                                    @RequestParam("position") final Byte position) {
        for (int[] servoPos : getBatchPositions(idBatch, position)) {
            sd21Servos.setPosition((byte) servoPos[0], servoPos[1]);
        }
    }
}
