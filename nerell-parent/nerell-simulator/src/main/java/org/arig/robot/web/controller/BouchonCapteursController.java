package org.arig.robot.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.Team;
import org.arig.robot.services.IOServiceBouchon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author gdepuille on 31/10/16.
 */
@Slf4j
@RequestMapping("/capteurs")
@RestController
public class BouchonCapteursController {

    @Autowired
    private IOServiceBouchon ioServiceBouchon;

    @RequestMapping(value = "/tirette", method = RequestMethod.POST)
    public void setTirette(@RequestBody Boolean value) {
        log.info("Définition de la valeur de la tirette : {}", value);
        ioServiceBouchon.setTirette(value);
    }

    @RequestMapping(value = "/team", method = RequestMethod.POST)
    public void setTeam(@RequestBody Boolean value) {
        Team team = value ? Team.JAUNE : Team.BLEU;
        log.info("Définition de la valeur de l'équipe : {}", team.name());
        ioServiceBouchon.setTeam(team);
    }

    @RequestMapping(value = "/au", method = RequestMethod.POST)
    public void setAu(@RequestBody Boolean value) {
        log.info("Définition de la valeur de l'arret d'urgence : {}", value);
        ioServiceBouchon.setAu(value);
    }
}
