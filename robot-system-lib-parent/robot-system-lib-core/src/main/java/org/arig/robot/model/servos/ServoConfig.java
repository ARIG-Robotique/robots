package org.arig.robot.model.servos;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
public class ServoConfig {

    private byte id;
    private String name;
    private int currentSpeed;
    private int currentPosition;

    private List<ServoPosition> positions = new ArrayList<>();

    public ServoConfig position(String name, int value) {
        positions.add(new ServoPosition(name, value));
        return this;
    }

}
