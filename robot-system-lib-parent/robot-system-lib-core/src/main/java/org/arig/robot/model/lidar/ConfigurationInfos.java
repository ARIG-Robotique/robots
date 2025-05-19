package org.arig.robot.model.lidar;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

/**
 * @author gdepuille on 03/03/17.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConfigurationInfos implements Serializable {
    private Boolean reversed = false;
    private Integer angleOffset = 0;
    private Integer excludeLowerThanMm = 150;
    private Integer excludeGreaterThanMm = 3600;
}
