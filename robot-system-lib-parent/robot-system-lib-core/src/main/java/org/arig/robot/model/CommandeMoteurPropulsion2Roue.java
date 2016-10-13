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
public class CommandeMoteurPropulsion2Roue {

    /**
     * The cmd droit.
     */
    private int droit;

    /**
     * The cmd gauche.
     */
    private int gauche;
}
