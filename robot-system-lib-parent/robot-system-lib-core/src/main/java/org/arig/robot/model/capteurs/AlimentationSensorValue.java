package org.arig.robot.model.capteurs;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true, chain = true)
public class AlimentationSensorValue {
    private double tension;
    private double current;
    private boolean fault;
}
