package org.arig.robot.communication.socket.balise;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
public class ZoneQueryData implements Serializable {

  @Data
  @AllArgsConstructor
  public static class Zone implements Serializable {
    private String name;
    private Integer cx;
    private Integer cy;
    private Integer dx;
    private Integer dy;
  }

  private List<ZoneQueryData.Zone> zones;

}
