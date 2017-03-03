package org.arig.robot.model.lidar.communication;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.arig.robot.model.lidar.communication.enums.LidarAction;
import org.arig.robot.model.lidar.communication.enums.LidarStatusResponse;

/**
 * @author gdepuille on 03/03/17.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractResponseWithDatas<D> extends AbstractResponse {
    private D datas;
}
