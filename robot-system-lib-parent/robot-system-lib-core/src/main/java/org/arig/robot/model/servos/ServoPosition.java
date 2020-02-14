package org.arig.robot.model.servos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ServoPosition {

    private String name;
    private int value;

}
