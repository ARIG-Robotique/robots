package org.arig.robot.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
public class PamiRobotStatus extends EurobotStatus {

  public PamiRobotStatus() {
    super(false, true);
  }

  @Setter(AccessLevel.NONE)
  private boolean showTime = false;

  public void enableShowTime() {
    if (!showTime) {
      log.info("[RS] Enabling show time");
      showTime = true;
    }
  }

  public void disableShowTime() {
    if (showTime) {
      log.info("[RS] Disabling show time");
      showTime = false;
    }
  }
}
