package org.arig.robot.services;

import org.arig.robot.model.bras.OptionBras;
import org.arig.robot.model.bras.PointBras;
import org.arig.robot.model.bras.PositionBras;
import org.arig.robot.model.bras.TransitionBras;
import org.arig.robot.utils.StateMachine;

import java.util.Map;

public abstract class AbstractBrasStateMachine extends StateMachine<PositionBras, PointBras, TransitionBras, OptionBras> {

    Map<Integer, PointBras> PRISE_STOCK = Map.of(
            1, new PointBras(-22, 136, -170),
            2, new PointBras(-7, 139, -170),
            3, new PointBras(8, 142, -170),
            4, new PointBras(23, 145, -170),
            5, new PointBras(38, 148, -170),
            6, new PointBras(53, 151, -170)
    );

    public AbstractBrasStateMachine(String name) {
        super(name);
        defaultTransition(TransitionBras.DEFAULT);
        current(PositionBras.INIT);
    }

}
