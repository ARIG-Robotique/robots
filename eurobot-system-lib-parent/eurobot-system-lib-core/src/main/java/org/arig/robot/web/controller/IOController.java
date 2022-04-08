package org.arig.robot.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.ConstantesConfig;
import org.arig.robot.services.CommonIOService;
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
    private CommonIOService ioService;

    @PostMapping("/pumps/{pump}")
    public void setPumpState(@PathVariable("pump") final String pump,
                             @RequestParam("state") final boolean state) {
        assert pump != null;
        switch (pump) {
            case "bas":
                if (state) {
                    ioService.enablePompeVentouseBas();
                } else {
                    ioService.releasePompeVentouseBas();
                }
                break;
            case "haut":
                if (state) {
                    ioService.enablePompeVentouseHaut();
                } else {
                    ioService.releasePompeVentouseHaut();
                }
                break;
        }
    }

}
