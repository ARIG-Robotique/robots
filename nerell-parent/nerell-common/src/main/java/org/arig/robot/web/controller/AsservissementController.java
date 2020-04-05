package org.arig.robot.web.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesConfig;
import org.arig.robot.filters.pid.IPidFilter;
import org.arig.robot.filters.ramp.IRampFilter;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/asservissement")
@AllArgsConstructor
@Profile(IConstantesConfig.profileMonitoring)
public class AsservissementController {

    private final IPidFilter pidDistance;
    private final IPidFilter pidOrientation;
    private final IRampFilter rampDistance;
    private final IRampFilter rampOrientation;


    @GetMapping("/{type}")
    public Map<String, Double> getPid(@PathVariable("type") final String type) {
        final Map<String, Double> values = new HashMap<>();

        if ("ANGLE".equals(type)) {
            values.putAll(pidOrientation.getTunings());
            values.putAll(rampOrientation.getRamps());

        } else if ("DIST".equals(type)) {
            values.putAll(pidDistance.getTunings());
            values.putAll(rampDistance.getRamps());

        } else {
            log.warn("Type d'asservissement invalide");
        }

        return values;
    }

    @PostMapping("/{type}")
    public void setPid(@PathVariable("type") final String type,
                       @RequestParam("kp") final double kp,
                       @RequestParam("ki") final double ki,
                       @RequestParam("kd") final double kd,
                       @RequestParam("rampAcc") final double rampAcc,
                       @RequestParam("rampDec") final double rampDec) {
        if ("ANGLE".equals(type)) {
            pidOrientation.setTunings(kp, ki, kd);
            pidOrientation.reset();
            rampOrientation.setRamps(rampAcc, rampDec);

        } else if ("DIST".equals(type)) {
            pidDistance.setTunings(kp, ki, kd);
            pidDistance.reset();
            rampDistance.setRamps(rampAcc, rampDec);

        } else {
            log.warn("Type d'asservissement invalide");
        }
    }
}
