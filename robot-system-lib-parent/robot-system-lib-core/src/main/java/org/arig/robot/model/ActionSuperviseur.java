package org.arig.robot.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ActionSuperviseur {
    int order;
    String name;
    boolean valid;
}
