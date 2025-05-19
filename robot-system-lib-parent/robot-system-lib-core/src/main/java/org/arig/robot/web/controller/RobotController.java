package org.arig.robot.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.ConstantesConfig;
import org.arig.robot.model.RobotName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author gdepuille on 13/01/15.
 */
@Slf4j
@RestController
@Profile(ConstantesConfig.profileMonitoring)
public class RobotController {

  @Autowired
  private RobotName robotName;

  @GetMapping("/robot")
  public Map<String, String> name() {
    Map<String, String> v = new LinkedHashMap<>();
    v.put("nom", robotName.name());
    v.put("version", robotName.version());

    return v;
  }
}
