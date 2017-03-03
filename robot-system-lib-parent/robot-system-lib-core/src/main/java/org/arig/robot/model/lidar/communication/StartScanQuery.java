package org.arig.robot.model.lidar.communication;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.arig.robot.model.lidar.SpeedInfos;
import org.arig.robot.model.lidar.communication.enums.LidarAction;

/**
 * @author gregorydepuille@sglk.local on 03/03/17.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StartScanQuery extends AbstractQueryWithDatas<SpeedInfos> {

    public StartScanQuery() {
        this(LidarAction.START_SCAN);
    }

    protected StartScanQuery(LidarAction action) {
        super(action);
    }

    public void setSpeed(Short speed) {
        if (speed == null) {
            return;
        }

        if (!hasDatas()) {
            setDatas(new SpeedInfos());
        }
        getDatas().setSpeed(speed);
    }
}
