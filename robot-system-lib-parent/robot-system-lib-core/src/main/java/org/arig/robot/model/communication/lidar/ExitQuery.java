package org.arig.robot.model.communication.lidar;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.arig.robot.model.communication.AbstractQuery;
import org.arig.robot.model.communication.lidar.enums.LidarAction;

@Data
@EqualsAndHashCode(callSuper = true)
public class ExitQuery extends AbstractQuery<LidarAction> {

    public ExitQuery() {
        super(LidarAction.EXIT);
    }

}
