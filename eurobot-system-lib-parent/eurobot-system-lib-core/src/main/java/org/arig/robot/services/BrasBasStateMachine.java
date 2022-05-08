package org.arig.robot.services;

import org.arig.robot.model.bras.ConfigBras;
import org.arig.robot.model.bras.PointBras;
import org.arig.robot.model.bras.PositionBras;
import org.arig.robot.model.bras.TransitionBras;

public class BrasBasStateMachine extends AbstractBrasStateMachine {

    public BrasBasStateMachine(ConfigBras configBras) {
        super("Bras bas");

        disableCheck(true);

        state(PositionBras.INIT, new PointBras(-22, 192, 90)); // dois matcher la position "Init" du service servos
        state(PositionBras.HORIZONTAL, new PointBras(configBras.x + configBras.r1 + configBras.r2 + configBras.r3, configBras.y, 0));

        state(PositionBras.REPOS_1, new PointBras(-10, 200, 90));
        state(PositionBras.REPOS_2, new PointBras(0, 208, 90));
        state(PositionBras.REPOS_3, new PointBras(18, 210, 90));
        state(PositionBras.REPOS_4, new PointBras(30, 215, 90));
        state(PositionBras.REPOS_5, new PointBras(50, 210, 90));
        state(PositionBras.REPOS_6, new PointBras(65, 200, 90));

        state(PositionBras.STOCK_DEPOSE_1, new PointBras(-18, 170, 180));
        state(PositionBras.STOCK_DEPOSE_2, new PointBras(-3, 170, 180));
        state(PositionBras.STOCK_DEPOSE_3, new PointBras(12, 170, 180));
        state(PositionBras.STOCK_DEPOSE_4, new PointBras(27, 170, 180));
        state(PositionBras.STOCK_DEPOSE_5, new PointBras(42, 170, 180));
        state(PositionBras.STOCK_DEPOSE_6, new PointBras(57, 170, 180));
        state(PositionBras.STOCK_PRISE_1, PRISE_STOCK.get(1));
        state(PositionBras.STOCK_PRISE_2, PRISE_STOCK.get(2));
        state(PositionBras.STOCK_PRISE_3, PRISE_STOCK.get(3));
        state(PositionBras.STOCK_PRISE_4, PRISE_STOCK.get(4));
        state(PositionBras.STOCK_PRISE_5, PRISE_STOCK.get(5));
        state(PositionBras.STOCK_PRISE_6, PRISE_STOCK.get(6));
        state(PositionBras.STOCK_ENTREE, new PointBras(135, 180, 90));

        state(PositionBras.SOL_PRISE, new PointBras(140, 20, -90));
        state(PositionBras.SOL_DEPOSE_1, new PointBras(192, 45, -80));
        state(PositionBras.SOL_DEPOSE_2, new PointBras(189, 55, -80));
        state(PositionBras.SOL_DEPOSE_3, new PointBras(182, 72, -80));
        state(PositionBras.SOL_DEPOSE_4, new PointBras(181, 88, -70));
        state(PositionBras.SOL_DEPOSE_5, new PointBras(154, 141, -40));

        state(PositionBras.BORDURE_APPROCHE, new PointBras(135, 140, -50));
        state(PositionBras.BORDURE_PRISE, new PointBras(149, 88, -90));
        state(PositionBras.ECHANGE_2, new PointBras(165, 150, 100));

        state(PositionBras.ECHANGE, new PointBras(147, 144, 100)); // en vrai c'est 90 mais le bras tombe sous le poids

        state(PositionBras.DISTRIBUTEUR_PRISE, new PointBras(210, 80, -20));
        state(PositionBras.GALERIE_DEPOSE, new PointBras(170, 164, 10));

        transition(PositionBras.INIT, PositionBras.STOCK_ENTREE);
        transition(PositionBras.STOCK_ENTREE, PositionBras.INIT);
        transition(PositionBras.INIT, PositionBras.HORIZONTAL);
        transition(PositionBras.HORIZONTAL, PositionBras.INIT);
        transition(PositionBras.HORIZONTAL, PositionBras.STOCK_ENTREE);
        transition(PositionBras.STOCK_ENTREE, PositionBras.HORIZONTAL);

        transition(PositionBras.REPOS_1, PositionBras.STOCK_ENTREE);
        transition(PositionBras.REPOS_2, PositionBras.STOCK_ENTREE);
        transition(PositionBras.REPOS_3, PositionBras.STOCK_ENTREE);
        transition(PositionBras.REPOS_4, PositionBras.STOCK_ENTREE);
        transition(PositionBras.REPOS_5, PositionBras.STOCK_ENTREE);
        transition(PositionBras.REPOS_6, PositionBras.STOCK_ENTREE);
        transition(PositionBras.STOCK_ENTREE, PositionBras.REPOS_1);
        transition(PositionBras.STOCK_ENTREE, PositionBras.REPOS_2);
        transition(PositionBras.STOCK_ENTREE, PositionBras.REPOS_3);
        transition(PositionBras.STOCK_ENTREE, PositionBras.REPOS_4);
        transition(PositionBras.STOCK_ENTREE, PositionBras.REPOS_5);
        transition(PositionBras.STOCK_ENTREE, PositionBras.REPOS_6);

        transition(PositionBras.REPOS_1, PositionBras.HORIZONTAL);
        transition(PositionBras.REPOS_2, PositionBras.HORIZONTAL);
        transition(PositionBras.REPOS_3, PositionBras.HORIZONTAL);
        transition(PositionBras.REPOS_4, PositionBras.HORIZONTAL);
        transition(PositionBras.REPOS_5, PositionBras.HORIZONTAL);
        transition(PositionBras.REPOS_6, PositionBras.HORIZONTAL);
        transition(PositionBras.HORIZONTAL, PositionBras.REPOS_1);
        transition(PositionBras.HORIZONTAL, PositionBras.REPOS_2);
        transition(PositionBras.HORIZONTAL, PositionBras.REPOS_3);
        transition(PositionBras.HORIZONTAL, PositionBras.REPOS_4);
        transition(PositionBras.HORIZONTAL, PositionBras.REPOS_5);
        transition(PositionBras.HORIZONTAL, PositionBras.REPOS_6);

        transition(PositionBras.SOL_PRISE, PositionBras.ECHANGE, TransitionBras.withPoints(
                new PointBras(170, 110, -30),
                new PointBras(144, 138, 90),
                new PointBras(135, 170, 100)
        ));
        transition(PositionBras.STOCK_ENTREE, PositionBras.ECHANGE);
        transition(PositionBras.HORIZONTAL, PositionBras.ECHANGE);
        transition(PositionBras.ECHANGE, PositionBras.HORIZONTAL);
        transition(PositionBras.ECHANGE, PositionBras.STOCK_ENTREE);

        transition(PositionBras.STOCK_DEPOSE_1, PositionBras.STOCK_ENTREE);
        transition(PositionBras.STOCK_ENTREE, PositionBras.STOCK_DEPOSE_1, TransitionBras.withPoints(
                new PointBras(70, 180, 120),
                new PointBras(30, 160, 160)
        ));
        transition(PositionBras.STOCK_DEPOSE_2, PositionBras.STOCK_ENTREE);
        transition(PositionBras.STOCK_ENTREE, PositionBras.STOCK_DEPOSE_2, TransitionBras.withPoints(
                new PointBras(70, 180, 120),
                new PointBras(30, 160, 160)
        ));
        transition(PositionBras.STOCK_DEPOSE_3, PositionBras.STOCK_ENTREE);
        transition(PositionBras.STOCK_ENTREE, PositionBras.STOCK_DEPOSE_3, TransitionBras.withPoints(
                new PointBras(70, 180, 120),
                new PointBras(30, 167, 170)
        ));
        transition(PositionBras.STOCK_DEPOSE_4, PositionBras.STOCK_ENTREE);
        transition(PositionBras.STOCK_ENTREE, PositionBras.STOCK_DEPOSE_4, TransitionBras.withPoints(
                new PointBras(70, 170, 150)
        ));
        transition(PositionBras.STOCK_DEPOSE_5, PositionBras.STOCK_ENTREE);
        transition(PositionBras.STOCK_ENTREE, PositionBras.STOCK_DEPOSE_5, TransitionBras.withPoints(
                new PointBras(105, 182, 130)
        ));
        transition(PositionBras.STOCK_DEPOSE_6, PositionBras.STOCK_ENTREE);
        transition(PositionBras.STOCK_ENTREE, PositionBras.STOCK_DEPOSE_6, TransitionBras.withPoints(
                new PointBras(110, 170, 140)
        ));

        transition(PositionBras.STOCK_PRISE_1, PositionBras.STOCK_ENTREE);
        transition(PositionBras.STOCK_ENTREE, PositionBras.STOCK_PRISE_1, TransitionBras.withPoints(
                PRISE_STOCK.get(2)
        ));
        transition(PositionBras.STOCK_PRISE_2, PositionBras.STOCK_ENTREE);
        transition(PositionBras.STOCK_ENTREE, PositionBras.STOCK_PRISE_2, TransitionBras.withPoints(
                PRISE_STOCK.get(3)
        ));
        transition(PositionBras.STOCK_PRISE_3, PositionBras.STOCK_ENTREE);
        transition(PositionBras.STOCK_ENTREE, PositionBras.STOCK_PRISE_3, TransitionBras.withPoints(
                PRISE_STOCK.get(4)
        ));
        transition(PositionBras.STOCK_PRISE_4, PositionBras.STOCK_ENTREE);
        transition(PositionBras.STOCK_ENTREE, PositionBras.STOCK_PRISE_4, TransitionBras.withPoints(
                PRISE_STOCK.get(5)
        ));
        transition(PositionBras.STOCK_PRISE_5, PositionBras.STOCK_ENTREE);
        transition(PositionBras.STOCK_ENTREE, PositionBras.STOCK_PRISE_5, TransitionBras.withPoints(
                PRISE_STOCK.get(6)
        ));
        transition(PositionBras.STOCK_PRISE_6, PositionBras.STOCK_ENTREE);
        transition(PositionBras.STOCK_ENTREE, PositionBras.STOCK_PRISE_6, TransitionBras.withPoints(
                new PointBras(70, 155, PRISE_STOCK.get(6).a)
        ));

        transition(PositionBras.STOCK_ENTREE, PositionBras.SOL_PRISE, TransitionBras.withPoints(
                new PointBras(140, 55, -90)
        ));
        transition(PositionBras.SOL_PRISE, PositionBras.STOCK_ENTREE, TransitionBras.withPoints(
                new PointBras(170, 110, -30),
                new PointBras(144, 138, 90),
                new PointBras(135, 170, 100)
        ));
        transition(PositionBras.SOL_PRISE, PositionBras.SOL_DEPOSE_1);
        transition(PositionBras.SOL_DEPOSE_1, PositionBras.SOL_PRISE);

        transition(PositionBras.STOCK_ENTREE, PositionBras.SOL_DEPOSE_1, TransitionBras.withPoints(
                new PointBras(160, 135, -40)
        ));
        transition(PositionBras.SOL_DEPOSE_1, PositionBras.STOCK_ENTREE);
        transition(PositionBras.STOCK_ENTREE, PositionBras.SOL_DEPOSE_2, TransitionBras.withPoints(
                new PointBras(160, 135, -40)
        ));
        transition(PositionBras.SOL_DEPOSE_2, PositionBras.STOCK_ENTREE);
        transition(PositionBras.STOCK_ENTREE, PositionBras.SOL_DEPOSE_3, TransitionBras.withPoints(
                new PointBras(160, 135, -40)
        ));
        transition(PositionBras.SOL_DEPOSE_3, PositionBras.STOCK_ENTREE);
        transition(PositionBras.STOCK_ENTREE, PositionBras.SOL_DEPOSE_4, TransitionBras.withPoints(
                new PointBras(160, 135, -40)
        ));
        transition(PositionBras.SOL_DEPOSE_4, PositionBras.STOCK_ENTREE);
        transition(PositionBras.STOCK_ENTREE, PositionBras.SOL_DEPOSE_5, TransitionBras.withPoints(
                new PointBras(139, 150, -40)
        ));
        transition(PositionBras.SOL_DEPOSE_5, PositionBras.STOCK_ENTREE);
        transition(PositionBras.ECHANGE, PositionBras.SOL_DEPOSE_1);

        transition(PositionBras.STOCK_ENTREE, PositionBras.BORDURE_APPROCHE);
        transition(PositionBras.BORDURE_APPROCHE, PositionBras.STOCK_ENTREE);
        transition(PositionBras.BORDURE_APPROCHE, PositionBras.BORDURE_PRISE);
        transition(PositionBras.BORDURE_APPROCHE, PositionBras.SOL_DEPOSE_1);
        transition(PositionBras.BORDURE_APPROCHE, PositionBras.SOL_PRISE);
        transition(PositionBras.BORDURE_PRISE, PositionBras.BORDURE_APPROCHE);
        transition(PositionBras.BORDURE_APPROCHE, PositionBras.ECHANGE_2);
        transition(PositionBras.ECHANGE_2, PositionBras.HORIZONTAL);
        transition(PositionBras.ECHANGE_2, PositionBras.STOCK_ENTREE);

        transition(PositionBras.STOCK_ENTREE, PositionBras.DISTRIBUTEUR_PRISE);
        transition(PositionBras.DISTRIBUTEUR_PRISE, PositionBras.STOCK_ENTREE);
        transition(PositionBras.DISTRIBUTEUR_PRISE, PositionBras.ECHANGE, TransitionBras.withPoints(
                new PointBras(190, 135, 0)
        ));
        transition(PositionBras.ECHANGE, PositionBras.DISTRIBUTEUR_PRISE);

        transition(PositionBras.STOCK_ENTREE, PositionBras.GALERIE_DEPOSE);
        transition(PositionBras.GALERIE_DEPOSE, PositionBras.STOCK_ENTREE);
    }

}
