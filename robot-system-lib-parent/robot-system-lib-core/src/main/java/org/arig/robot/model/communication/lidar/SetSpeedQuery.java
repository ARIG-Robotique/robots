package org.arig.robot.model.communication.lidar;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.arig.robot.model.communication.lidar.enums.LidarAction;

@Data
@EqualsAndHashCode(callSuper = true)
public class SetSpeedQuery extends StartScanQuery {

    public SetSpeedQuery(short speed) {
        super(LidarAction.SET_SPEED);
        this.setSpeed(speed);
    }
}
