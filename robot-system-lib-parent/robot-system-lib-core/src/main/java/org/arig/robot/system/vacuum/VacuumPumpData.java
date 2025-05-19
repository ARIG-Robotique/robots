package org.arig.robot.system.vacuum;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true, chain = true)
public class VacuumPumpData {
  private boolean tor;
  private boolean presence;
  private int vacuum;
}
