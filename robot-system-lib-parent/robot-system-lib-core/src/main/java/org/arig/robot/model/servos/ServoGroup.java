package org.arig.robot.model.servos;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
public class ServoGroup {

    private int id;
    private String name;
    private List<ServoPosition> batch = new ArrayList<>();
    private List<ServoConfig> servos = new ArrayList<>();

    public ServoGroup servo(ServoConfig s) {
        servos.add(s);
        return this;
    }

    public ServoGroup batch(String name, int position) {
        batch.add(new ServoPosition(name, position));
        return this;
    }

}
