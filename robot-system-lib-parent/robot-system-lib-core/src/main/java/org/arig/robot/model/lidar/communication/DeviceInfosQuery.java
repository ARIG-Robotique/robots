package org.arig.robot.model.lidar.communication;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.arig.robot.model.lidar.communication.enums.LidarAction;

/**
 * @author gdepuille on 03/03/17.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DeviceInfosQuery extends AbstractQuery {

    public DeviceInfosQuery() {
        super(LidarAction.DEVICE_INFO);
    }
}
