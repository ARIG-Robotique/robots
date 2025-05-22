package org.arig.robot.model;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class ConstructionFloorAction extends ConstructionAction {

  private final Etage etage;
  private final StockPosition stockPosition;

  public ConstructionFloorAction(Face face, Rang rang, Etage etage, StockPosition stockPosition) {
    super(ConstructionActionType.PLACE_ONE, face, rang);
    this.etage = etage;
    this.stockPosition = stockPosition;
  }

  public String toString() {
    return String.format("Action %s, Face %s, %s, %s, Stock %s", type(), face(), rang(), etage, stockPosition);
  }
}
