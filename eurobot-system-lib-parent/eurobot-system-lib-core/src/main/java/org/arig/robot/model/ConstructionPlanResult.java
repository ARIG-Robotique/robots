package org.arig.robot.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(fluent = true)
public class ConstructionPlanResult {

  private final ConstructionArea newArea;
  private final List<ConstructionAction> actions;

}
