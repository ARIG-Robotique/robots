package org.arig.robot.model.servos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

@Data
@Accessors(chain = true, fluent = true)
public class Servo {

    @JsonProperty("id")
    private byte id;
    @JsonProperty("name")
    private String name;
    private int time;

    @JsonProperty("positions")
    private Map<String, ServoPosition> positions = new HashMap<>();
    private int min = 3000;
    private int max = 0;

    // monitoring
    @JsonProperty("currentSpeed")
    private int currentSpeed;
    @JsonProperty("currentPosition")
    private int currentPosition;

    public Servo position(String name, int value) {
        return position(name, value, 0);
    }

    public Servo position(String name, int value, int speed) {
        positions.put(name, new ServoPosition().name(name).value(value).speed((byte) speed));
        min = Math.min(min, value);
        max = Math.max(max, value);
        return this;
    }

}
