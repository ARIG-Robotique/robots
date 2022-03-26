package org.arig.robot.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(fluent = true, chain = true)
public class CarreFouille implements Serializable {

    private final int numero;

    private CouleurCarreFouille couleur;
    private boolean bascule = false;

    public CarreFouille(final int numero) {
        this(numero, CouleurCarreFouille.INCONNU);
    }

    public CarreFouille(final int numero, final CouleurCarreFouille couleur) {
        this.numero = numero;
        this.couleur = couleur;
    }

    public boolean needRead() {
        return couleur == null || couleur == CouleurCarreFouille.INCONNU;
    }

    /**
     * Coordonnée en X du carre de fouille
     * @return Valeur de X
     */
    public double getX() {
        // 667.5 du bord de la table sur l'axe X
        // 185 entraxe des carres de fouille
        return 667.5 + ((numero - 1) * 185);
    }
}
