package org.arig.robot.model.lidar;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author gdepuille on 03/03/17.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScanInfos implements Serializable {
    private List<Scan> scan;
    private Short ignored;
    private Integer tailleObstacle = null;
}
