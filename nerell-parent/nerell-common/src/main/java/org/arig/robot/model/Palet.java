package org.arig.robot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true, fluent = true)
@NoArgsConstructor
@AllArgsConstructor
public class Palet {

    public enum Couleur {
        INCONNU, ROUGE, VERT, BLEU, GOLD
    }

    private Integer numero;

    /**
     * Indique si la couleur a été validée avec le lecteur couleur
     */
    private boolean valide;

    private Couleur couleur;

    public static Palet rouge() {
        return new Palet().couleur(Couleur.ROUGE).valide(true);
    }

    public static Palet vert() {
        return new Palet().couleur(Couleur.VERT).valide(true);
    }

    public static Palet bleu() {
        return new Palet().couleur(Couleur.BLEU).valide(true);
    }

    public static Palet gold() {
        return new Palet().couleur(Couleur.GOLD).valide(true);
    }

}
