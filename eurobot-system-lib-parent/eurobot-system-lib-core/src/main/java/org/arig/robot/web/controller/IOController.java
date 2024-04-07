package org.arig.robot.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.ConstantesConfig;
import org.arig.robot.services.CommonRobotIOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/io")
@Profile(ConstantesConfig.profileMonitoring)
public class IOController {

    @Autowired
    private CommonRobotIOService ioService;

    @PostMapping("/electro-aimant")
    public void setElectroAimant(@RequestParam("state") final String state) {
        if ("on".equals(state)) {
            ioService.enableElectroAimant();
        } else {
            ioService.disableElectroAimant();
        }
    }

    @PostMapping("/solar-wheel")
    public void setSolarWheel(@RequestParam("state") final String state, @RequestParam("speed") final int speed) {
        switch(state) {
            case "bleu":
                ioService.tournePanneauBleu(speed);
                break;
            case "jaune":
                ioService.tournePanneauJaune(speed);
                break;
            default:
                ioService.stopTournePanneau();
        }
    }

}
