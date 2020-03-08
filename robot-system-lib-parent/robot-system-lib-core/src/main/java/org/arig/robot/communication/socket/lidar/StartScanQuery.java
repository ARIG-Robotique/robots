package org.arig.robot.communication.socket.lidar;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.arig.robot.communication.socket.AbstractQueryWithDatas;
import org.arig.robot.communication.socket.lidar.enums.LidarAction;
import org.arig.robot.model.lidar.SpeedInfos;

@Data
@EqualsAndHashCode(callSuper = true)
public class StartScanQuery extends AbstractQueryWithDatas<LidarAction, SpeedInfos> {

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
