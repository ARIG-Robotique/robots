package org.arig.eurobot.model.servos;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author gdepuille on 01/05/15.
 */
@Data
@AllArgsConstructor
public class ServoPositionDTO {

    private String name;
    private int value;

}
