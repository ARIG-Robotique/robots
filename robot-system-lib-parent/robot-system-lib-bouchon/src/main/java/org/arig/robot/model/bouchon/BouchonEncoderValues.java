package org.arig.robot.model.bouchon;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author gdepuille on 31/10/16.
 */
@Data
@Accessors(fluent = true, chain = true)
public class BouchonEncoderValues {

    private int vitesseMoteur;
    private double gauche, droit;
}
