package org.arig.robot.strategy;

import org.arig.robot.model.Point;
import org.arig.robot.model.Position;
import org.arig.robot.model.RobotConfig;
import org.arig.robot.services.TrajectoryManager;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.arig.robot.utils.TableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

public abstract class AbstractMacroAction extends AbstractAction {

  @Autowired
  protected RobotConfig robotConfig;

  @Autowired
  protected TrajectoryManager mv;

  @Autowired
  protected ConvertionRobotUnit conv;

  @Autowired
  @Qualifier("currentPosition")
  protected Position position;

  @Autowired
  protected TableUtils tableUtils;

  protected List<AbstractAction> actions;

  @Override
  public Point entryPoint() {
    return actions.get(0).entryPoint();
  }

  @Override
  public boolean isValid() {
    return actions.stream().allMatch(AbstractAction::isValid);
  }

  @Override
  public void execute() {
    for (AbstractAction action : actions) {
      action.execute();

      // FIXME détection d'action annullée
      if (!action.isCompleted() && !action.isTimeValid()) {
        break;
      }
    }

    complete();
  }

}
