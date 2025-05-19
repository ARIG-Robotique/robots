package org.arig.robot.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.services.PamiIOServiceSimulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class PamiSimulatorCapteursController {

  @Autowired
  private PamiIOServiceSimulator ioServiceBouchon;

  @PostMapping(AbstractCapteursController.ROOT_PATH + "/tirette")
  public void setTirette(@RequestBody Boolean value) {
    log.info("Définition de la valeur de la tirette : {}", value);
    ioServiceBouchon.tirette(value);
  }

  @PostMapping(AbstractCapteursController.ROOT_PATH + "/au")
  public void setAu(@RequestBody Boolean value) {
    log.info("Définition de la valeur de l'arret d'urgence : {}", value);
    ioServiceBouchon.au(value);
  }
}
