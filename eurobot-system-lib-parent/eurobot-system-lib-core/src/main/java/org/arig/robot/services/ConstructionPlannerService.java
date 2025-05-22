package org.arig.robot.services;

import org.arig.robot.model.ConstructionAction;
import org.arig.robot.model.ConstructionArea;
import org.arig.robot.model.ConstructionPlanResult;

import java.util.List;

public interface ConstructionPlannerService {

  ConstructionPlanResult plan(ConstructionArea area);

}
