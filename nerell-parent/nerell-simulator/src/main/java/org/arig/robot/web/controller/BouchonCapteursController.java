package org.arig.robot.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.services.NerellIOServiceBouchon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class BouchonCapteursController extends CapteursController {

    @Autowired
    private NerellIOServiceBouchon ioServiceBouchon;

    @PostMapping(value = "/tirette")
    public void setTirette(@RequestBody Boolean value) {
        log.info("Définition de la valeur de la tirette : {}", value);
        ioServiceBouchon.tirette(value);
    }

    @PostMapping(value = "/au")
    public void setAu(@RequestBody Boolean value) {
        log.info("Définition de la valeur de l'arret d'urgence : {}", value);
        ioServiceBouchon.au(value);
    }
}
