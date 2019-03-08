package org.arig.robot.model.communication.lidar;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.arig.robot.model.communication.AbstractQuery;
import org.arig.robot.model.communication.lidar.enums.LidarAction;

@Data
@EqualsAndHashCode(callSuper = true)
public class HealthInfosQuery extends AbstractQuery<LidarAction> {

    public HealthInfosQuery() {
        super(LidarAction.HEALTH_INFO);
    }

}
