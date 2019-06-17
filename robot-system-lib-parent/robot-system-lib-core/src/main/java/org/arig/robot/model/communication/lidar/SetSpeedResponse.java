package org.arig.robot.model.communication.lidar;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.arig.robot.model.communication.AbstractResponse;
import org.arig.robot.model.communication.lidar.enums.LidarAction;

@Data
@EqualsAndHashCode(callSuper = true)
public class SetSpeedResponse extends AbstractResponse<LidarAction> { }
