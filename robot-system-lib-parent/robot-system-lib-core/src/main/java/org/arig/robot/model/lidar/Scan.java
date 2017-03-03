package org.arig.robot.model.lidar;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * @author gdepuille on 03/03/17.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Scan {
    private float angleDeg;
    private float distanceMm;
    private boolean syncBit;
    private short quality;
}
