package org.arig.robot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(fluent = true, chain = true)
public class PanneauSolaire implements Serializable {

    private final int numero;

    @JsonProperty("color")
    private CouleurPanneauSolaire couleur;

    @JsonIgnore
    @Setter(AccessLevel.NONE)
    private byte nbTry = 0;

    public PanneauSolaire(final int numero) {
        this(numero, CouleurPanneauSolaire.AUCUNE);
    }

    public PanneauSolaire(final int numero, final CouleurPanneauSolaire couleur) {
        this.numero = numero;
        this.couleur = couleur;
    }

    public void incrementTry() {
        nbTry++;
    }

    public void decrementTry() {
        nbTry--;
    }

    public boolean besoinDeTourner(Team team) {
        if (team == Team.JAUNE) {
            return couleur == CouleurPanneauSolaire.BLEU || couleur == CouleurPanneauSolaire.AUCUNE;
        } else {
            return couleur == CouleurPanneauSolaire.JAUNE || couleur == CouleurPanneauSolaire.AUCUNE;
        }
    }

    public boolean couleurValide(Team team) {
        if (couleur == CouleurPanneauSolaire.AUCUNE) return false;
        if (couleur == CouleurPanneauSolaire.JAUNE_ET_BLEU) return true;
        if (team == Team.JAUNE) {
            return couleur == CouleurPanneauSolaire.JAUNE;
        } else {
            return couleur == CouleurPanneauSolaire.BLEU;
        }
    }

    /**
     * Coordonnée en X du panneau solaire
     * @return Valeur de X
     */
    @JsonIgnore
    public int getX() {
        // 275 du bord de la table sur l'axe X
        // 225 entraxe des carres de fouille
        // 550 entre 3 et 4, et entre 6 et 7

        int entraxeComplementaire = 0;
        if (numero > 3) {
            entraxeComplementaire += 325;
        }
        if (numero > 6) {
            entraxeComplementaire += 325;
        }
        return 275 + entraxeComplementaire + ((numero - 1) * 225);
    }

    @Override
    public String toString() {
        return "PanneauSolaire{" + "numero=" + numero + ", couleur=" + couleur + "}";
    }
}
