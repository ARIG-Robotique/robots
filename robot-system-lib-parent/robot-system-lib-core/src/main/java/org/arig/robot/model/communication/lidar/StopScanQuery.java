package org.arig.robot.model.communication.lidar;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.arig.robot.model.communication.AbstractQuery;
import org.arig.robot.model.communication.lidar.enums.LidarAction;

@Data
@EqualsAndHashCode(callSuper = true)
public class StopScanQuery extends AbstractQuery<LidarAction> {

    public StopScanQuery() {
        super(LidarAction.STOP_SCAN);
    }

}
