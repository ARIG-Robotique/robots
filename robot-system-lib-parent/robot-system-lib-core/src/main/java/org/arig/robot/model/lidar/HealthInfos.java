package org.arig.robot.model.lidar;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.arig.robot.model.lidar.enums.HealthState;

import java.io.Serializable;

/**
 * @author gdepuille on 03/03/17.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class HealthInfos implements Serializable {
    private HealthState state;
    private Short value;
    private Short errorCode;

    public boolean isOk() {
        return state != null && state == HealthState.OK;
    }
}
