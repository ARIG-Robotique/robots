package org.arig.robot.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(fluent = true, chain = true)
public class CarreFouille implements Serializable {

    private final int numero;

    @JsonProperty("color")
    private CouleurCarreFouille couleur;

    @JsonProperty("bascule")
    private boolean bascule = false;

    @Setter(AccessLevel.NONE)
    private byte nbTry = 0;

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

    public void incrementTry() {
        nbTry++;
    }

    public void decrementTry() {
        nbTry--;
    }

    /**
     * Coordonnée en X du carre de fouille
     * @return Valeur de X
     */
    public int getX() {
        // 667 du bord de la table sur l'axe X
        // 185 entraxe des carres de fouille
        return 667 + ((numero - 1) * 185);
    }

    @Override
    public String toString() {
        return "CarreFouille{" + "numero=" + numero + ", couleur=" + couleur + ", bascule=" + bascule + "}";
    }
}
