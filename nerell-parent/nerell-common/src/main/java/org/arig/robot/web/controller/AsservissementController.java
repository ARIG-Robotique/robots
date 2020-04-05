package org.arig.robot.web.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesConfig;
import org.arig.robot.filters.pid.IPidFilter;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/asservissement")
@AllArgsConstructor
@Profile(IConstantesConfig.profileMonitoring)
public class AsservissementController {

    private final IPidFilter pidDistance;
    private final IPidFilter pidOrientation;

    @GetMapping("/pid/{type}")
    public Map<String, Double> getPid(@PathVariable("type") final String type) {
        if ("ANGLE".equals(type)) {
            return pidOrientation.getTunings();
        } else if ("DIST".equals(type)) {
            return pidDistance.getTunings();
        } else {
            log.warn("Type de PID invalide");
            return null;
        }
    }

    @PostMapping("/pid/{type}")
    public void setPid(@PathVariable("type") final String type,
                       @RequestParam("kp") final double kp,
                       @RequestParam("ki") final double ki,
                       @RequestParam("kd") final double kd) {
        if ("ANGLE".equals(type)) {
            pidOrientation.setTunings(kp, ki, kd);
        } else if ("DIST".equals(type)) {
            pidDistance.setTunings(kp, ki, kd);
        } else {
            log.warn("Type de PID invalide");
        }
    }
}
