package org.arig.robot.model;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class ConstructionMoveAction extends ConstructionAction {

  public ConstructionMoveAction(Face face, Rang rang) {
    super(ConstructionActionType.MOVE, face, rang);
  }

  public String toString() {
    return String.format("Action %s, Face %s, %s", type(), face(), rang());
  }
}
