package org.arig.robot.services;

import org.arig.robot.model.bras.OptionBras;
import org.arig.robot.model.bras.PointBras;
import org.arig.robot.model.bras.PositionBras;
import org.arig.robot.model.bras.TransitionBras;
import org.arig.robot.utils.StateMachine;

import java.util.Map;

public abstract class AbstractBrasStateMachine extends StateMachine<PositionBras, PointBras, TransitionBras, OptionBras> {

    Map<Integer, PointBras> PRISE_STOCK = Map.of(
            1, new PointBras(-14, 135, -175),
            2, new PointBras(4, 136, -175),
            3, new PointBras(17, 140, -175),
            4, new PointBras(34, 141, -175),
            5, new PointBras(49, 139, -175),
            6, new PointBras(63, 138, -175)
    );

    public AbstractBrasStateMachine(String name) {
        super(name);
        defaultTransition(TransitionBras.DEFAULT);
        current(PositionBras.INIT);
    }

}
