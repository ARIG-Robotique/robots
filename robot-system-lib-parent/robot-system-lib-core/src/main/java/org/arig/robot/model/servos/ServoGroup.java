package org.arig.robot.model.servos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ServoGroup {
    private int order;
    private String name;
}
