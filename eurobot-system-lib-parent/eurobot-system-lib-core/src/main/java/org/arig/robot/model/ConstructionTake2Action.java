package org.arig.robot.model;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class ConstructionTake2Action extends ConstructionAction {

  private final Etage etage;

  public ConstructionTake2Action(Face face, Rang rang) {
    super(ConstructionActionType.TAKE_TWO, face, rang);
    this.etage = Etage.ETAGE_1;
  }

  public String toString() {
    return String.format("Action %s, Face %s, %s, %s", type(), face(), rang(), etage);
  }
}
