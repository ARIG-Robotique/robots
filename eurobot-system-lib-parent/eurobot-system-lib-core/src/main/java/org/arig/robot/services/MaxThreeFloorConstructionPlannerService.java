package org.arig.robot.services;

import lombok.RequiredArgsConstructor;
import org.arig.robot.model.ConstructionAction;
import org.arig.robot.model.ConstructionArea;
import org.arig.robot.model.ConstructionElementSource;
import org.arig.robot.model.ConstructionPlanResult;
import org.arig.robot.model.EurobotStatus;
import org.arig.robot.model.Rang;
import org.arig.robot.model.StockVirtuel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MaxThreeFloorConstructionPlannerService implements ConstructionPlannerService {

  private final EurobotStatus rs;

  @Override
  public ConstructionPlanResult plan(ConstructionArea area) {
    return new ConstructionPlanResult(area.clone(), new ArrayList<>());
  }
}
