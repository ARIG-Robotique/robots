package org.arig.robot.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.ConstantesConfig;
import org.arig.robot.services.CommonRobotIOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PathVariable;
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

    @PostMapping("/electro-aimant/{state}")
    public void setPumpState(@PathVariable("state") final boolean state) {
        if (state) {
            ioService.enableElectroAimant();
        } else {
            ioService.disableElectroAimant();
        }
    }

    @PostMapping("/solar-wheel/{state}")
    public void setPumpState(@PathVariable("state") final String state, @RequestParam("speed") final int speed) {
        switch(state) {
            case "avant":
                ioService.tournePanneauAvant(speed);
                break;
            case "arriere":
                ioService.tournePanneauArriere(speed);
                break;
            default:
                ioService.stopTournePanneau();
        }
    }

}
