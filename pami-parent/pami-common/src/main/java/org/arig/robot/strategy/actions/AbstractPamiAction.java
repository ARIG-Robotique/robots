package org.arig.robot.strategy.actions;

import org.arig.robot.model.PamiRobotStatus;
import org.arig.robot.services.PamiIOService;
import org.arig.robot.services.PamiRobotServosService;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractPamiAction extends AbstractEurobotAction {

  @Autowired
  protected PamiRobotStatus rsOdin;

  @Autowired
  protected PamiRobotServosService servosOdin;

  @Autowired
  protected PamiIOService ioService;

}
