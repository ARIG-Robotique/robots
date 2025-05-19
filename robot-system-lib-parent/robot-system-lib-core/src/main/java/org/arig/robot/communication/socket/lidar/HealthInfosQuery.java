package org.arig.robot.communication.socket.lidar;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.arig.robot.communication.socket.AbstractQuery;
import org.arig.robot.communication.socket.lidar.enums.LidarAction;

@Data
@EqualsAndHashCode(callSuper = true)
public class HealthInfosQuery extends AbstractQuery<LidarAction> {

  public HealthInfosQuery() {
    super(LidarAction.HEALTH_INFO);
  }

}
