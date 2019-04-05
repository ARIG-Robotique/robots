package org.arig.robot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true, fluent = true)
@NoArgsConstructor
@AllArgsConstructor
public class Palet {

    @AllArgsConstructor
    public enum Couleur {
        ANY(0),
        INCONNU(0),
        ROUGE(1),
        VERT(2),
        BLEU(3),
        GOLD(4);

        @Getter
        private int importance;
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
