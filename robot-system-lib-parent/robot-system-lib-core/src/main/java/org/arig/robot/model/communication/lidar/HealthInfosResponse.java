package org.arig.robot.model.communication.lidar;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.arig.robot.model.communication.AbstractResponseWithDatas;
import org.arig.robot.model.communication.lidar.enums.LidarAction;
import org.arig.robot.model.lidar.HealthInfos;

@Data
@EqualsAndHashCode(callSuper = true)
public class HealthInfosResponse extends AbstractResponseWithDatas<LidarAction, HealthInfos> { }