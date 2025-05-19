package org.arig.robot.model.lidar;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

/**
 * @author gdepuille on 03/03/17.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpeedInfos implements Serializable {
  private Short speed;
}
