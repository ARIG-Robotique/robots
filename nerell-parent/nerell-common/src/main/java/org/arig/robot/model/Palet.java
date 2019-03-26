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
        ANY, INCONNU, ROUGE, VERT, BLEU, GOLD
    }

    private Integer numero;

    private Couleur couleur;

    public static Palet rouge() {
        return new Palet().couleur(Couleur.ROUGE);
    }

    public static Palet vert() {
        return new Palet().couleur(Couleur.VERT);
    }

    public static Palet bleu() {
        return new Palet().couleur(Couleur.BLEU);
    }

    public static Palet gold() {
        return new Palet().couleur(Couleur.GOLD);
    }

}
