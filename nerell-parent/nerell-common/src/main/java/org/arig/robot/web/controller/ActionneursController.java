package org.arig.robot.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesConfig;
import org.arig.robot.model.ESide;
import org.arig.robot.model.EState;
import org.arig.robot.services.IIOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author gdepuille on 21/12/13.
 */
@Slf4j
@RestController
@RequestMapping("/actionneurs")
@Profile(IConstantesConfig.profileMonitoring)
public class ActionneursController {


    @Autowired
    private IIOService ioService;

    @PostMapping(value = "/ev/{side}")
    public void electrovanne(@PathVariable final ESide side, @RequestParam("state") final EState state) {
        log.info("Electrovanne {} : {}", side.name(), state.name());

        if (side == ESide.DROITE) {
            if (state == EState.ON) {
                ioService.airElectroVanneDroite();
            } else {
                ioService.videElectroVanneDroite();
            }
        } else {
            if (state == EState.ON) {
                ioService.airElectroVanneGauche();
            } else {
                ioService.videElectroVanneGauche();
            }
        }
    }

    @PostMapping(value = "/pompe/{side}")
    public void pompe(@PathVariable final ESide side, @RequestParam("state") final EState state) {
        log.info("Pompe a vide {} : {}", side.name(), state.name());

        if (side == ESide.DROITE) {
            if (state == EState.ON) {
                ioService.enablePompeAVideDroite();
            } else {
                ioService.disablePompeAVideDroite();
            }
        } else {
            if (state == EState.ON) {
                ioService.enablePompeAVideGauche();
            } else {
                ioService.disablePompeAVideGauche();
            }
        }
    }
}
