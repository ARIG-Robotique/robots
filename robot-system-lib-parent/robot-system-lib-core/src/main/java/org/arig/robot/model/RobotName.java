package org.arig.robot.model;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author gdepuille on 13/10/16.
 */
@Data
@Builder
@Accessors(fluent = true, chain = true)
public class RobotName {

    public enum RobotIdentification {
        NERELL, ODIN, TINKER
    }

    private String name;
    private final RobotIdentification id;
    private final String version;

}
