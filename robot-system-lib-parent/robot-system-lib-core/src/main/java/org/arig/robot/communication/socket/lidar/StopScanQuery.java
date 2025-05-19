package org.arig.robot.communication.socket.lidar;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.arig.robot.communication.socket.AbstractQuery;
import org.arig.robot.communication.socket.lidar.enums.LidarAction;

@Data
@EqualsAndHashCode(callSuper = true)
public class StopScanQuery extends AbstractQuery<LidarAction> {

  public StopScanQuery() {
    super(LidarAction.STOP_SCAN);
  }

}
