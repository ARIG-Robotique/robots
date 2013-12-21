package org.arig.prehistobot.web;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.II2CManager;
import org.arig.robot.system.servos.SD21Servos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by mythril on 21/12/13.
 */
@Profile("i2c")
@RestController
@RequestMapping("/servos")
@Slf4j
public class ServosController {

    @Autowired
    private SD21Servos sd21Servos;

    @RequestMapping("/{idServo}")
    public void servosPositionAndSpeed(
            @RequestPart(required = true, value = "idServo") final Byte idServo,
            @RequestParam(required = true, value = "position") final Integer position,
            @RequestParam(required = false, value = "speed") final Byte speed) {

        log.info(String.format("Modification du servo moteur %d : Pos -> %s ; Speed -> %s", idServo, position, speed));

        sd21Servos.setPositionAndSpeed(idServo, speed, position);
    }
}
