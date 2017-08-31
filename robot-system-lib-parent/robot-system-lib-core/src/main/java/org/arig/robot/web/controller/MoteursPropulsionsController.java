package org.arig.robot.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesConfig;
import org.arig.robot.system.motors.AbstractPropulsionsMotors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author gdepuille on 21/12/13.
 */
@Slf4j
@Profile(IConstantesConfig.profileMonitoring)
@RestController
@RequestMapping("/moteurs/propulsion")
public class MoteursPropulsionsController {

    @Autowired
    private AbstractPropulsionsMotors motors;

    @RequestMapping(method = RequestMethod.GET)
    public Map<String, Integer> getMoteurs() {
        Map<String, Integer> res = new HashMap<>();
        res.put("droit", motors.currentSpeedDroit());
        res.put("gauche", motors.currentSpeedGauche());

        return res;
    }

    @RequestMapping(value = "/droit/{speed}", method = RequestMethod.POST)
    public void setMoteurDroit(@PathVariable(required = true) final Integer speed) {
        log.info("Modification de la vitesse du moteur droit : " + speed);
        motors.moteurDroit(speed);
    }

    @RequestMapping(value = "/gauche/{speed}", method = RequestMethod.POST)
    public void setMoteurGauche(@PathVariable(required = true) final Integer speed) {
        log.info("Modification de la vitesse du moteur gauche : " + speed);
        motors.moteurGauche(speed);
    }
}
