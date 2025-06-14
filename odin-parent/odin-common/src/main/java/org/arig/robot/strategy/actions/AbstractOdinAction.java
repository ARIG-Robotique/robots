package org.arig.robot.strategy.actions;

import org.arig.robot.model.OdinRobotStatus;
import org.arig.robot.services.OdinIOService;
import org.arig.robot.services.OdinRobotServosService;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractOdinAction extends AbstractEurobotAction {

  @Autowired
  protected OdinRobotStatus rsOdin;

  @Autowired
  protected OdinRobotServosService servosOdin;

  @Autowired
  protected OdinIOService ioService;

}
