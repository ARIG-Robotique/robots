package org.arig.robot.strategy.actions;

import org.arig.robot.model.PamiRobotStatus;
import org.arig.robot.strategy.AbstractMacroAction;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractPamiMacroAction extends AbstractMacroAction {

  @Autowired
  protected PamiRobotStatus rs;

}
