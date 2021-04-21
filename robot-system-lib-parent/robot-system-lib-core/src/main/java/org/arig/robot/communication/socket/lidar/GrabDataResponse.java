package org.arig.robot.communication.socket.lidar;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.arig.robot.communication.socket.AbstractResponseWithData;
import org.arig.robot.communication.socket.lidar.enums.LidarAction;
import org.arig.robot.model.lidar.ScanInfos;

@Data
@EqualsAndHashCode(callSuper = true)
public class GrabDataResponse extends AbstractResponseWithData<LidarAction, ScanInfos> { }
