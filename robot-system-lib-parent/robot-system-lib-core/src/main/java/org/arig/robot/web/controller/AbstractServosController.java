package org.arig.robot.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesConfig;
import org.arig.robot.model.servos.ServoConfig;
import org.arig.robot.system.servos.SD21Servos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author gdepuille on 13/10/16.
 */
@Slf4j
@RequestMapping("/servos")
@Profile(IConstantesConfig.profileMonitoring)
public abstract class AbstractServosController {

    @Autowired
    protected SD21Servos sd21Servos;

    protected abstract List<ServoConfig> servosConfig();

    @GetMapping
    public final Map<String, List<ServoConfig>> config() {
        return servosConfig().stream()
                .sorted((servo1, servo2) -> {
                    if (servo1.getGroup().getOrder() == servo2.getGroup().getOrder()) {
                        return servo1.getName().compareToIgnoreCase(servo2.getName());
                    }

                    return servo1.getGroup().getOrder() - servo2.getGroup().getOrder();
                })
                .collect(Collectors.groupingBy(s -> s.getGroup().getName()));
    }

    @GetMapping(value = "/{idServo}")
    public final ServoConfig getServoPositionAndSpeed(@PathVariable("idServo") final Byte idServo) {
        return servosConfig().stream()
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
}
