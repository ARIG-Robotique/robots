package org.arig.robot.system.motion;

/**
 * Created by gdepuille on 05/01/15.
 */
public interface IAsservissementPolaire extends IAsservissement {

    /**
     * Valeur permettant de définir que le robot est en approche de la distance spécifié
     *
     * @return La valeur calculé de la fenetre d'approche
     */
    double getFenetreApprocheDistance();

    /**
     * Valeur permettant de définir que le robot est en approche de l'orientation spécifié
     *
     * @return La valeur calculé de la fenetre d'approche
     */
    double getFenetreApprocheOrientation();
}
