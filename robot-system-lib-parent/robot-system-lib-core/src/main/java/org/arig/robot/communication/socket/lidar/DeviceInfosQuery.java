package org.arig.robot.communication.socket.lidar;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.arig.robot.communication.socket.AbstractQuery;
import org.arig.robot.communication.socket.lidar.enums.LidarAction;

@Data
@EqualsAndHashCode(callSuper = true)
public class DeviceInfosQuery extends AbstractQuery<LidarAction> {

  public DeviceInfosQuery() {
    super(LidarAction.DEVICE_INFO);
  }

}
