package org.arig.robot.communication.socket.lidar;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.arig.robot.communication.socket.AbstractResponseWithDatas;
import org.arig.robot.communication.socket.lidar.enums.LidarAction;
import org.arig.robot.model.lidar.DeviceInfos;

@Data
@EqualsAndHashCode(callSuper = true)
public class DeviceInfosResponse extends AbstractResponseWithDatas<LidarAction, DeviceInfos> {}
