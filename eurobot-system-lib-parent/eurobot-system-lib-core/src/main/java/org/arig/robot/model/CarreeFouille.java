package org.arig.robot.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(fluent = true, chain = true)
public class CarreeFouille implements Serializable {

    private final int numero;

    private CouleurCarreeFouille couleur;
    private boolean bascule = false;

    public CarreeFouille(final int numero) {
        this(numero, CouleurCarreeFouille.INCONNU);
    }

    public CarreeFouille(final int numero, final CouleurCarreeFouille couleur) {
        this.numero = numero;
        this.couleur = couleur;
    }

    public boolean needRead() {
        return couleur == null || couleur == CouleurCarreeFouille.INCONNU;
    }

    public boolean isValid() {
        return couleur != null && couleur != CouleurCarreeFouille.INTERDIT;
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
