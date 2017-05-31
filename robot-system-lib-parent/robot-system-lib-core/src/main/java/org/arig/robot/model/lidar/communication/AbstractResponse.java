package org.arig.robot.model.lidar.communication;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.arig.robot.model.lidar.communication.enums.LidarAction;
import org.arig.robot.model.lidar.communication.enums.LidarStatusResponse;

/**
 * @author gdepuille on 03/03/17.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class AbstractResponse {

    private LidarStatusResponse status;
    private LidarAction action;
    private String errorMessage;
}
