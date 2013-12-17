package org.arig.robot.system.motion;

/**
 * The Interface IAsservissement.
 * 
 * @author mythril
 */
public interface IAsservissement {

    /**
     * Process.
     */
    void process();

    /**
     * Reset.
     */
    void reset();

    /**
     * Reset.
     * 
     * @param resetFilters
     *            the reset filters
     */
    void reset(final boolean resetFilters);

    /**
     * Valeur permettant de définir que le robot est en approche de la distance spécifié
     * @return
     */
    double getFenetreApprocheDistance();

    /**
     * Valeur permettant de définir que le robot est en approche de l'orientation spécifié
     * @return
     */
    double getFenetreApprocheOrientation();
}
