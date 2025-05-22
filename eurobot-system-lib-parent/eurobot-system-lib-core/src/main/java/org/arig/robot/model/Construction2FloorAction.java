package org.arig.robot.model;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class Construction2FloorAction extends ConstructionAction {

  private final Etage etage;

  public Construction2FloorAction(Face face, Rang rang) {
    super(ConstructionActionType.PLACE_TWO, face, rang);
    this.etage = Etage.ETAGE_2;
  }

  public String toString() {
    return String.format("Action %s, Face %s, %s, %s", type(), face(), rang(), etage);
  }
}
