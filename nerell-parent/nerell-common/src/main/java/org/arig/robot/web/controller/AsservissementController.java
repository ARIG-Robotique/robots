package org.arig.robot.web.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesConfig;
import org.arig.robot.filters.pid.IPidFilter;
import org.arig.robot.filters.ramp.IRampFilter;
import org.arig.robot.system.TrajectoryManager;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/asservissement")
@AllArgsConstructor
@Profile(IConstantesConfig.profileMonitoring)
public class AsservissementController {

    private final TrajectoryManager mv;
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
            values.put("vitesse", (double) mv.vitesseOrientation());

        } else if ("DIST".equals(type)) {
            values.putAll(pidDistance.getTunings());
            values.putAll(rampDistance.getRamps());
            values.put("vitesse", (double) mv.vitesseDistance());

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
                       @RequestParam("rampDec") final double rampDec,
                       @RequestParam("vitesse") final double vitesse) {
        if ("ANGLE".equals(type)) {
            pidOrientation.setTunings(kp, ki, kd);
            pidOrientation.reset();
            rampOrientation.setRamps(rampAcc, rampDec);
            log.info("Vitesse orientation {}", vitesse);
            mv.setVitesse(mv.vitesseDistance(), (long) vitesse);

        } else if ("DIST".equals(type)) {
            pidDistance.setTunings(kp, ki, kd);
            pidDistance.reset();
            rampDistance.setRamps(rampAcc, rampDec);
            log.info("Vitesse distance {}", vitesse);
            mv.setVitesse((long) vitesse, mv.vitesseOrientation());

        } else {
            log.warn("Type d'asservissement invalide");
        }
    }
}
