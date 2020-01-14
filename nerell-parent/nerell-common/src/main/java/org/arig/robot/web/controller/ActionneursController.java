package org.arig.robot.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesConfig;
import org.arig.robot.model.EState;
import org.arig.robot.services.IIOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/actionneurs")
@Profile(IConstantesConfig.profileMonitoring)
public class ActionneursController {

    @Autowired
    private IIOService ioService;

    @PostMapping(value = "/ev")
    public void electrovanne(@RequestParam("state") final EState state) {
        log.info("Electrovanne : {}", state.name());

        if (state == EState.ON) {
            ioService.airElectroVanneAvant();
        } else {
            ioService.videElectroVanneAvant();
        }
    }

    @PostMapping(value = "/pompe}")
    public void pompe(@RequestParam("state") final EState state) {
        log.info("Pompe a vide : {}", state.name());

        if (state == EState.ON) {
            ioService.enablePompeAVideAvant();
        } else {
            ioService.disablePompeAVideAvant();
        }
    }
}
