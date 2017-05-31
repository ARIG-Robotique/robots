package org.arig.robot.model.servos;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author gdepuille on 01/05/15.
 */
@Data
@AllArgsConstructor
public class ServoPosition {

    private String name;
    private int value;

}
