package org.arig.robot.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesConfig;
import org.arig.robot.system.motors.AbstractPropulsionsMotors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author gdepuille on 21/12/13.
 */
@Slf4j
@RestController
@RequestMapping("/moteurs/propulsion")
@Profile(IConstantesConfig.profileMonitoring)
public class MoteursPropulsionsController {

    @Autowired
    private AbstractPropulsionsMotors motors;

    @GetMapping
    public Map<String, Integer> getMoteurs() {
        Map<String, Integer> res = new HashMap<>();
        res.put("droit", motors.currentSpeedDroit());
        res.put("gauche", motors.currentSpeedGauche());

        return res;
    }

    @PostMapping(value = "/droit/{speed}")
    public void setMoteurDroit(@PathVariable final Integer speed) {
        log.info("Modification de la vitesse du moteur droit : " + speed);
        motors.moteurDroit(speed);
    }

    @PostMapping(value = "/gauche/{speed}")
    public void setMoteurGauche(@PathVariable final Integer speed) {
        log.info("Modification de la vitesse du moteur gauche : " + speed);
        motors.moteurGauche(speed);
    }
}
