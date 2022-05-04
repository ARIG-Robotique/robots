package org.arig.robot.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.system.capteurs.CarreFouilleReader;

@Slf4j
@Data
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
public class OdinRobotStatus extends EurobotStatus {

    public OdinRobotStatus(CarreFouilleReader carreFouilleReader) {
        super(false, carreFouilleReader);
    }
}
