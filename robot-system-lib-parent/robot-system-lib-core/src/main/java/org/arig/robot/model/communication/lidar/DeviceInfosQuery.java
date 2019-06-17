package org.arig.robot.model.communication.lidar;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.arig.robot.model.communication.AbstractQuery;
import org.arig.robot.model.communication.lidar.enums.LidarAction;

@Data
@EqualsAndHashCode(callSuper = true)
public class DeviceInfosQuery extends AbstractQuery<LidarAction> {

    public DeviceInfosQuery() {
        super(LidarAction.DEVICE_INFO);
    }

}
