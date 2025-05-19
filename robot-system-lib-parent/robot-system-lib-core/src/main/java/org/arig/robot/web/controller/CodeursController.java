package org.arig.robot.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.ConstantesConfig;
import org.arig.robot.system.encoders.Abstract2WheelsEncoders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author gdepuille on 22/12/14.
 */
@Slf4j
@RestController
@RequestMapping("/codeurs")
@Profile(ConstantesConfig.profileMonitoring)
public class CodeursController {

  @Autowired
  private Abstract2WheelsEncoders encoders;

  @GetMapping
  public Map<String, Double> showValues() {
    encoders.lectureValeurs();
    Map<String, Double> v = new HashMap<>();
    v.put("distance", encoders.getDistance());
    v.put("orientation", encoders.getOrientation());
    v.put("gauche", encoders.getGauche());
    v.put("droit", encoders.getDroit());

    return v;
  }

  @GetMapping(value = "/reset")
  public void resetValues() {
    encoders.reset();
  }
}
