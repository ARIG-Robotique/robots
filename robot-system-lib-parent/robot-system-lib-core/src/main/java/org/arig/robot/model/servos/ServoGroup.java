package org.arig.robot.model.servos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true, fluent = true)
public class ServoGroup {

  @JsonProperty("id")
  private byte id;
  @JsonProperty("name")
  private String name;
  @JsonProperty("servos")
  private List<Servo> servos = new ArrayList<>();
  @JsonProperty("batch")
  private List<String> batch = new ArrayList<>();

  public ServoGroup addServo(Servo s) {
    servos.add(s);
    return this;
  }

  public ServoGroup batch(String name) {
    batch.add(name);
    return this;
  }

}
