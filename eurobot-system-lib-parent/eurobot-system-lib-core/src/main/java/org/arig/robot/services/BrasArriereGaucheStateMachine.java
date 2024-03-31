package org.arig.robot.services;

import org.arig.robot.model.bras.ConfigBras;
import org.arig.robot.model.bras.PointBras;
import org.arig.robot.model.bras.PositionBras;

public class BrasArriereGaucheStateMachine extends AbstractBrasStateMachine {

    public BrasArriereGaucheStateMachine(ConfigBras configBras) {
        super("Bras arriere gauche");

        disableCheck(true);

        state(PositionBras.INIT, new PointBras(110, 60, -90)); // dois matcher la position "Init" du service servos
        state(PositionBras.HORIZONTAL, new PointBras(configBras.x + configBras.r1 + configBras.r2 + configBras.r3, configBras.y, 0));
    }

}
