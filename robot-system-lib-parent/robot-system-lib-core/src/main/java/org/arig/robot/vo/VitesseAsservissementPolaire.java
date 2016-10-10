package org.arig.robot.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by mythril on 29/12/13.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VitesseAsservissementPolaire {

    /** The vitesse distance. */
    private long distance;

    /** The vitesse orientation. */
    private long orientation;
}
