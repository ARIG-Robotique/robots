package org.arig.robot.model.lidar.communication;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.arig.robot.model.lidar.communication.enums.LidarAction;

/**
 * @author gregorydepuille@sglk.local on 03/03/17.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GrabDataQuery extends AbstractQuery {

    public GrabDataQuery() {
        super(LidarAction.GRAB_DATA);
    }

}
