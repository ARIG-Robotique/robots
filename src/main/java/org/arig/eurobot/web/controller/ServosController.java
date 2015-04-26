package org.arig.eurobot.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.system.servos.SD21Servos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by mythril on 21/12/13.
 */
@Slf4j
@Profile("raspi")
@RestController
@RequestMapping("/servos")
public class ServosController {

    @Autowired
    private SD21Servos sd21Servos;

    @RequestMapping("/{idServo}")
    public void servosPositionAndSpeed(
            @PathVariable("idServo") final Byte idServo,
            @RequestParam("position") final Integer position,
            @RequestParam(value = "speed", required = false) final Byte speed) {

        log.info("Modification du servo moteur {} : Pos -> {} ; Speed -> {}", idServo, position, speed);

        if (speed != null) {
            sd21Servos.setPositionAndSpeed(idServo, position, speed);
        } else {
            sd21Servos.setPosition(idServo, position);
        }
    }
}
