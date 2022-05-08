package org.arig.robot.services;

import org.arig.robot.model.bras.ConfigBras;
import org.arig.robot.model.bras.PointBras;
import org.arig.robot.model.bras.PositionBras;
import org.arig.robot.model.bras.TransitionBras;

public class BrasHautStateMachine extends AbstractBrasStateMachine {

    public BrasHautStateMachine(ConfigBras configBras) {
        super("Bras haut");

        disableCheck(true);

        state(PositionBras.INIT, new PointBras(66, 97, -70)); // dois matcher la position "Init" du service servos
        state(PositionBras.HORIZONTAL, new PointBras(configBras.x + configBras.r1 + configBras.r2 + configBras.r3, configBras.y, 0));

        state(PositionBras.REPOS_1, new PointBras(80, 95, -70));
        state(PositionBras.REPOS_2, new PointBras(75, 95, -70));
        state(PositionBras.REPOS_3, new PointBras(95, 100, -80));
        state(PositionBras.REPOS_4, new PointBras(95, 100, -80));
        state(PositionBras.REPOS_5, new PointBras(115, 110, -80));
        state(PositionBras.REPOS_6, new PointBras(120, 120, -70));

        state(PositionBras.STOCK_DEPOSE_1, new PointBras(-20, 165, 180));
        state(PositionBras.STOCK_DEPOSE_2, new PointBras(-4, 165, 180));
        state(PositionBras.STOCK_DEPOSE_3, new PointBras(12, 165, 180));
        state(PositionBras.STOCK_DEPOSE_4, new PointBras(30, 165, 180));
        state(PositionBras.STOCK_DEPOSE_5, new PointBras(46, 170, 180));
        state(PositionBras.STOCK_DEPOSE_6, new PointBras(65, 170, 180));
        state(PositionBras.STOCK_PRISE_1, PRISE_STOCK.get(1));
        state(PositionBras.STOCK_PRISE_2, PRISE_STOCK.get(2));
        state(PositionBras.STOCK_PRISE_3, PRISE_STOCK.get(3));
        state(PositionBras.STOCK_PRISE_4, PRISE_STOCK.get(4));
        state(PositionBras.STOCK_PRISE_5, PRISE_STOCK.get(5));
        state(PositionBras.STOCK_PRISE_6, PRISE_STOCK.get(6));
        state(PositionBras.STOCK_ENTREE, new PointBras(100, 170, -160));

        state(PositionBras.ECHANGE, new PointBras(160, 155, -90));

        state(PositionBras.GALERIE_DEPOSE, new PointBras(configBras.x + configBras.r1 + configBras.r2 + configBras.r3, configBras.y, 0));
        state(PositionBras.GALERIE_DEPOSE_CENTRE, new PointBras(213, 220, 0));

        transition(PositionBras.INIT, PositionBras.HORIZONTAL);
        transition(PositionBras.HORIZONTAL, PositionBras.INIT);
        transition(PositionBras.INIT, PositionBras.STOCK_ENTREE);
        transition(PositionBras.STOCK_ENTREE, PositionBras.INIT);
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

        transition(PositionBras.HORIZONTAL, PositionBras.ECHANGE, TransitionBras.withPoints(
                new PointBras(180, 185, -90)
        ));
        transition(PositionBras.ECHANGE, PositionBras.HORIZONTAL);
        transition(PositionBras.ECHANGE, PositionBras.STOCK_ENTREE);
        transition(PositionBras.STOCK_ENTREE, PositionBras.ECHANGE);

        transition(PositionBras.STOCK_DEPOSE_1, PositionBras.STOCK_ENTREE);
        transition(PositionBras.STOCK_ENTREE, PositionBras.STOCK_DEPOSE_1, TransitionBras.withPoints(
                new PointBras(55, 170, 170)
        ));
        transition(PositionBras.STOCK_DEPOSE_2, PositionBras.STOCK_ENTREE);
        transition(PositionBras.STOCK_ENTREE, PositionBras.STOCK_DEPOSE_2, TransitionBras.withPoints(
                new PointBras(55, 170, 170)
        ));
        transition(PositionBras.STOCK_DEPOSE_3, PositionBras.STOCK_ENTREE);
        transition(PositionBras.STOCK_ENTREE, PositionBras.STOCK_DEPOSE_3, TransitionBras.withPoints(
                new PointBras(55, 170, 170)
        ));
        transition(PositionBras.STOCK_DEPOSE_4, PositionBras.STOCK_ENTREE);
        transition(PositionBras.STOCK_ENTREE, PositionBras.STOCK_DEPOSE_4, TransitionBras.withPoints(
                new PointBras(55, 170, 170)
        ));
        transition(PositionBras.STOCK_DEPOSE_5, PositionBras.STOCK_ENTREE);
        transition(PositionBras.STOCK_ENTREE, PositionBras.STOCK_DEPOSE_5, TransitionBras.withPoints(
                new PointBras(70, 170, 170)
        ));
        transition(PositionBras.STOCK_DEPOSE_6, PositionBras.STOCK_ENTREE);
        transition(PositionBras.STOCK_ENTREE, PositionBras.STOCK_DEPOSE_6);

        transition(PositionBras.STOCK_PRISE_1, PositionBras.STOCK_ENTREE);
        transition(PositionBras.STOCK_ENTREE, PositionBras.STOCK_PRISE_1);
        transition(PositionBras.STOCK_PRISE_2, PositionBras.STOCK_ENTREE);
        transition(PositionBras.STOCK_ENTREE, PositionBras.STOCK_PRISE_2);
        transition(PositionBras.STOCK_PRISE_3, PositionBras.STOCK_ENTREE);
        transition(PositionBras.STOCK_ENTREE, PositionBras.STOCK_PRISE_3);
        transition(PositionBras.STOCK_PRISE_4, PositionBras.STOCK_ENTREE);
        transition(PositionBras.STOCK_ENTREE, PositionBras.STOCK_PRISE_4);
        transition(PositionBras.STOCK_PRISE_5, PositionBras.STOCK_ENTREE);
        transition(PositionBras.STOCK_ENTREE, PositionBras.STOCK_PRISE_5);
        transition(PositionBras.STOCK_PRISE_6, PositionBras.STOCK_ENTREE);
        transition(PositionBras.STOCK_ENTREE, PositionBras.STOCK_PRISE_6);

        transition(PositionBras.STOCK_ENTREE, PositionBras.GALERIE_DEPOSE);
        transition(PositionBras.GALERIE_DEPOSE, PositionBras.STOCK_ENTREE);
        transition(PositionBras.STOCK_ENTREE, PositionBras.GALERIE_DEPOSE_CENTRE);
        transition(PositionBras.GALERIE_DEPOSE_CENTRE, PositionBras.STOCK_ENTREE);
    }

}
