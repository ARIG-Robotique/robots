package org.arig.robot.model.bras;

public enum PositionBras {
    // commun
    INIT,
    HORIZONTAL,
    STOCK_ENTREE,
    ECHANGE,

    REPOS_1,
    REPOS_2,
    REPOS_3,
    REPOS_4,
    REPOS_5,
    REPOS_6,

    STOCK_PRISE_1,
    STOCK_PRISE_2,
    STOCK_PRISE_3,
    STOCK_PRISE_4,
    STOCK_PRISE_5,
    STOCK_PRISE_6,

    STOCK_DEPOSE_1,
    STOCK_DEPOSE_2,
    STOCK_DEPOSE_3,
    STOCK_DEPOSE_4,
    STOCK_DEPOSE_5,
    STOCK_DEPOSE_6,

    GALERIE_DEPOSE,
    GALERIE_DEPOSE_MILIEU,

    // bas
    SOL_PRISE,
    SOL_DEPOSE,

    BORDURE_APPROCHE,
    BORDURE_PRISE,
    ECHANGE_2,

    DISTRIBUTEUR_PRISE,
    ;

    public static PositionBras stockPrise(int indexStock) {
        return PositionBras.valueOf("STOCK_PRISE_" + (indexStock+1));
    }

    public static PositionBras stockDepose(int indexStock) {
        return PositionBras.valueOf("STOCK_DEPOSE_" + (indexStock+1));
    }

    public static PositionBras repos(int tailleStock) {
        if (tailleStock == 0) {
            return INIT;
        }
        return PositionBras.valueOf("REPOS_" + tailleStock);
    }
}
