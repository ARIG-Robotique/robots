package org.arig.eurobot.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.arig.eurobot.constants.IConstantesSpringConfig;
import org.arig.eurobot.model.system.RobotInfo;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by gdepuille on 13/01/15.
 */
@Slf4j
@Profile(IConstantesSpringConfig.profileMonitoring)
@RestController
@RequestMapping(value = "/robot")
public class RobotController {

    @RequestMapping("/name")
    public Map<String, String> name() {
        Map<String, String> v = new LinkedHashMap<>();
        v.put("nom", "Elfa : Robomovies 2015");
        v.put("version", "1.0.0");

        return v;
    }

    @RequestMapping("/system")
    public RobotInfo system() {
        return RobotInfo.getInstance();
    }
}
