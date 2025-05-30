package org.arig.robot.model;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class Construction3FloorsAction extends ConstructionAction {

  private final Etage etage;
  private final Rang rangReprise;

  public Construction3FloorsAction(Face face, Rang rang, Rang rangReprise) {
    super(ConstructionActionType.MAKE_THREE, face, rang);
    this.etage = Etage.ETAGE_2;
    this.rangReprise = rangReprise;
  }

  public String toString() {
    return String.format("Action %s, Face %s, %s, %s", type(), face(), rang(), etage);
  }
}
