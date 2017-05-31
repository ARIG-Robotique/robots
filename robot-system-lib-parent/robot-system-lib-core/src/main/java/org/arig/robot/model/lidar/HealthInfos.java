package org.arig.robot.model.lidar;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.arig.robot.model.lidar.enums.HealthState;

/**
 * @author gdepuille on 03/03/17.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class HealthInfos {
    private HealthState state;
    private Short value;
    private Short errorCode;

    public boolean isOk() {
        return state != null && state == HealthState.OK;
    }
}
