package org.arig.robot.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author gdepuille on 13/10/16.
 */
@Data
@Accessors(fluent = true, chain = true)
public class RobotName {

    private String name;
    private String version;

}
