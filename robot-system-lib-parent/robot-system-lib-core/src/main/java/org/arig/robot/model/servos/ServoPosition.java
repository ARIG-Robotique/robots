package org.arig.robot.model.servos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true, fluent = true)
public class ServoPosition {

  @JsonProperty("name")
  private String name;
  @JsonProperty("value")
  private int value;
  @JsonProperty("speed")
  private byte speed;

}
