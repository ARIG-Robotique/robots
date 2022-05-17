package org.arig.robot.model.bras;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PositionBras {
    // commun
    INIT(true),
    HORIZONTAL(false),
    STOCK_ENTREE(true),
    ECHANGE(false),

    REPOS_1(true),
    REPOS_2(true),
    REPOS_3(true),
    REPOS_4(true),
    REPOS_5(true),
    REPOS_6(true),

    STOCK_PRISE_1(true),
    STOCK_PRISE_2(true),
    STOCK_PRISE_3(true),
    STOCK_PRISE_4(true),
    STOCK_PRISE_5(true),
    STOCK_PRISE_6(true),

    STOCK_DEPOSE_1(true),
    STOCK_DEPOSE_2(true),
    STOCK_DEPOSE_3(true),
    STOCK_DEPOSE_4(true),
    STOCK_DEPOSE_5(true),
    STOCK_DEPOSE_6(true),

    GALERIE_DEPOSE(false),

    // bas
    SOL_PRISE(false),
    SOL_DEPOSE_1(false),
    SOL_DEPOSE_2(false),
    SOL_DEPOSE_3(false),
    SOL_DEPOSE_4(false),
    SOL_DEPOSE_5(false),

    BORDURE_APPROCHE(false),
    BORDURE_PRISE(false),
    ECHANGE_2(false),

    DISTRIBUTEUR_PRISE_1(false),
    DISTRIBUTEUR_PRISE_2(false),
    DISTRIBUTEUR_PRISE_3(false),
    ;

    private final boolean inside;

    public static PositionBras stockPrise(int indexStock) {
        return PositionBras.valueOf("STOCK_PRISE_" + (indexStock + 1));
    }

    public static PositionBras stockDepose(int indexStock) {
        return PositionBras.valueOf("STOCK_DEPOSE_" + (indexStock + 1));
    }

    public static PositionBras repos(int tailleStock) {
        if (tailleStock == 0) {
            return INIT;
        }
        return PositionBras.valueOf("REPOS_" + tailleStock);
    }

    public static PositionBras solDepose(int tailleCampement) {
        return PositionBras.valueOf("SOL_DEPOSE_" + (tailleCampement + 1));
    }

    public static PositionBras distribPrise(int i) {
        return PositionBras.valueOf("DISTRIBUTEUR_PRISE_" + (i + 1));
    }
}
