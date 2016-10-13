package org.arig.robot.model.servos;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author gdepuille on 14/10/16.
 */
@Data
@Accessors(chain = true)
public class ServoInfo {

    private byte id;
    private String name;
    private int currentSpeed;
    private int currentPosition;

}
