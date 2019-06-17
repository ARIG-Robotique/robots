package org.arig.robot.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Couleur d'un palet atom factory
 */
@AllArgsConstructor
public enum CouleurPalet {
    ANY(0),
    INCONNU(0),
    ROUGE(1),
    VERT(2),
    BLEU(3),
    GOLD(4);

    @Getter
    private int importance;
}
