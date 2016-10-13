package org.arig.robot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author gdepuille on 29/12/13.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConsigneAsservissementPolaire {

    /**
     * The consigne distance.
     */
    private long distance;

    /**
     * The consigne orientation.
     */
    private long orientation;
}
