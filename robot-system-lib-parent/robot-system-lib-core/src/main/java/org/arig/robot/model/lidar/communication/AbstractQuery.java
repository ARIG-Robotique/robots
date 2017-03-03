package org.arig.robot.model.lidar.communication;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.arig.robot.model.lidar.communication.enums.LidarAction;

/**
 * @author gdepuille
 */
@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractQuery {

    private final LidarAction action;
}
