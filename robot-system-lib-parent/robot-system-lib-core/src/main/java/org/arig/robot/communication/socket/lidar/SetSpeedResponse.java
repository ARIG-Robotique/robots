package org.arig.robot.communication.socket.lidar;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.arig.robot.communication.socket.AbstractResponse;
import org.arig.robot.communication.socket.lidar.enums.LidarAction;

@Data
@EqualsAndHashCode(callSuper = true)
public class SetSpeedResponse extends AbstractResponse<LidarAction> {
}
