package org.arig.robot.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.ConstantesConfig;
import org.arig.robot.services.CommonRobotIOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/io")
@Profile(ConstantesConfig.profileMonitoring)
public class IOController {

    @Autowired
    private CommonRobotIOService ioService;

    @PostMapping("/pumps/{pump}")
    public void setPumpState(@PathVariable("pump") final String pump,
                             @RequestParam("state") final boolean state) {
        assert pump != null;
        switch (pump) {
            case "bas":

                break;
            case "haut":

                break;
        }
    }

}
