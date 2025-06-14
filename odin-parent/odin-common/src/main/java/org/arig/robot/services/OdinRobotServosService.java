package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.system.servos.i2c.SD21Servos;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OdinRobotServosService extends AbstractCommonRobotServosService {
  public OdinRobotServosService(SD21Servos sd21Servos) {
    super(sd21Servos);
  }
}
