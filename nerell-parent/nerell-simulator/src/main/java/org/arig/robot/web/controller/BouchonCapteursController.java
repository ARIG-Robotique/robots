package org.arig.robot.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.Team;
import org.arig.robot.services.IOServiceBouchon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author gdepuille on 31/10/16.
 */
@Slf4j
@RestController
@RequestMapping("/capteurs")
public class BouchonCapteursController {

    @Autowired
    private IOServiceBouchon ioServiceBouchon;

    @PostMapping(value = "/tirette")
    public void setTirette(@RequestBody Boolean value) {
        log.info("Définition de la valeur de la tirette : {}", value);
        ioServiceBouchon.setTirette(value);
    }

    @PostMapping(value = "/team")
    public void setTeam(@RequestBody Boolean value) {
        Team team = value ? Team.JAUNE : Team.BLEU;
        log.info("Définition de la valeur de l'équipe : {}", team.name());
        ioServiceBouchon.setTeam(team);
    }

    @PostMapping(value = "/au")
    public void setAu(@RequestBody Boolean value) {
        log.info("Définition de la valeur de l'arret d'urgence : {}", value);
        ioServiceBouchon.setAu(value);
    }
}
