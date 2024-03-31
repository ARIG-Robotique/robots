package org.arig.robot.services;

import org.arig.robot.model.bras.OptionBras;
import org.arig.robot.model.bras.PointBras;
import org.arig.robot.model.bras.PositionBras;
import org.arig.robot.model.bras.TransitionBras;
import org.arig.robot.utils.StateMachine;

public abstract class AbstractBrasStateMachine extends StateMachine<PositionBras, PointBras, TransitionBras, OptionBras> {

    public AbstractBrasStateMachine(String name) {
        super(name);
        defaultTransition(TransitionBras.DEFAULT);
        current(PositionBras.INIT);
    }

}
