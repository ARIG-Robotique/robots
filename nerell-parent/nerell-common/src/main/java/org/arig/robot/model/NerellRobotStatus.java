package org.arig.robot.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
public class NerellRobotStatus extends EurobotStatus {

  public NerellRobotStatus() {
    super(true);
  }
}
