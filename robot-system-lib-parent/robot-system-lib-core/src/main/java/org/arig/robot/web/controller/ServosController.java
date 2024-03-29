package org.arig.robot.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.ConstantesConfig;
import org.arig.robot.model.servos.ServoGroup;
import org.arig.robot.services.AbstractServosService;
import org.arig.robot.system.servos.SD21Servos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/servos")
@Profile(ConstantesConfig.profileMonitoring)
public class ServosController {

    @Autowired
    protected AbstractServosService servos;

    @GetMapping
    public final List<ServoGroup> config() {
        return servos.getGroups();
    }

    @PostMapping(value = "/{idServo}")
    public final void servoPositionAndSpeed(
            @PathVariable("idServo") final Byte idServo,
            @RequestParam("position") final Integer position,
            @RequestParam(value = "speed", required = false) final Byte speed) {

        if (speed != null) {
            log.info("Modification du servo moteur {} : Pos -> {} ; Speed -> {}", idServo, position, speed);
            servos.getDevice(idServo).setPositionAndSpeed(idServo, position, speed);
        } else {
            log.info("Modification du servo moteur {} : Pos -> {}", idServo, position);
            servos.getDevice(idServo).setPosition(idServo, position);
        }
    }

    @PostMapping({"/batch"})
    public final void batchPosition(@RequestParam("group") final String group,
                                    @RequestParam("position") final String position) {
        servos.setPositionBatch(group, position, false);
    }
}
