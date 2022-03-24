package org.arig.robot.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(fluent = true)
public class CarreeFouille implements Serializable {

    private final int numero;

    @JsonProperty("couleur")
    private CouleurCarreeFouille couleur;

    @Setter(value = AccessLevel.NONE)
    private boolean bascule = false;

    public CarreeFouille(final int numero) {
        this(numero, CouleurCarreeFouille.INCONNU);
    }

    public CarreeFouille(final int numero, final CouleurCarreeFouille couleur) {
        this.numero = numero;
        this.couleur = couleur;
    }

    /**
     * Marque le carre de fouille comme basculé
     */
    public void setBascule() {
        this.bascule = true;
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
        // 185 entraxe des carre de fouille
        return 667.5 + (numero * 185) - 185;
    }
}
