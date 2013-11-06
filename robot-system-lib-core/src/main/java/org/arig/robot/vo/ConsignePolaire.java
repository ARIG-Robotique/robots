package org.arig.robot.vo;

import lombok.Data;

/**
 * The Class ConsignePolaire.
 * 
 * @author mythril
 */
@Data
public class ConsignePolaire {

    /** The consigne distance. */
    private long consigneDistance;

    /** The vitesse distance. */
    private long vitesseDistance;

    /** The consigne orientation. */
    private long consigneOrientation;

    /** The vitesse orientation. */
    private long vitesseOrientation;

    /** The cmd droit. */
    private int cmdDroit;

    /** The cmd gauche. */
    private int cmdGauche;

    /** The frein. */
    boolean frein;

    /**
     * Instantiates a new consigne polaire.
     */
    public ConsignePolaire() {
        consigneDistance = 0;
        consigneOrientation = 0;
        vitesseDistance = 100;
        vitesseOrientation = 100;
        cmdDroit = 0;
        cmdGauche = 0;
        frein = true;
    }
}
