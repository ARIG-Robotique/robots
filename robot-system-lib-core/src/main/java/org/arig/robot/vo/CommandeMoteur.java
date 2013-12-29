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
public class CommandeMoteur {

    /** The cmd droit. */
    private int droit;

    /** The cmd gauche. */
    private int gauche;
}
