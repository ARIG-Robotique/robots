package org.arig.prehistobot.web;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.system.motors.MD22Motors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by mythril on 21/12/13.
 */
@Profile("raspi")
@RestController
@RequestMapping("/motors")
@Slf4j
public class MotorsControllers {

    @Autowired
    private MD22Motors md22Motors;

    @RequestMapping("/droit")
    public void setMotorDroit(@RequestParam(required = true) final Integer speed) {
        log.info("Modification de la vitesse du moteur droit : " + speed);
        md22Motors.moteurDroit(speed);
    }

    @RequestMapping("/gauche")
    public void setMotorGauche(@RequestParam(required = true) final Integer speed) {
        log.info("Modification de la vitesse du moteur gauche : " + speed);
        md22Motors.moteurGauche(speed);
    }
}
