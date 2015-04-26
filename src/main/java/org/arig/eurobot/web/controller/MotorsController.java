package org.arig.eurobot.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.system.motors.AbstractPropulsionsMotors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by mythril on 21/12/13.
 */
@Slf4j
@Profile("raspi")
@RestController
@RequestMapping("/motors")
public class MotorsController {

    @Autowired
    private AbstractPropulsionsMotors motors;

    @RequestMapping("/droit")
    public void setMotorDroit(@RequestParam(required = true) final Integer speed) {
        log.info("Modification de la vitesse du moteur droit : " + speed);
        motors.moteurDroit(speed);
    }

    @RequestMapping("/gauche")
    public void setMotorGauche(@RequestParam(required = true) final Integer speed) {
        log.info("Modification de la vitesse du moteur gauche : " + speed);
        motors.moteurGauche(speed);
    }
}
