package org.arig.robot.model.lidar;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Scan implements Serializable {
  private float angleDeg;
  private float distanceMm;
  private boolean syncBit;
  private short quality;

  public Scan offsetDistanceMm(int offsetDistanceMm) {
    if (offsetDistanceMm == 0) {
      return this;
    }
    return new Scan(angleDeg, distanceMm + offsetDistanceMm, syncBit, quality);
  }
}
