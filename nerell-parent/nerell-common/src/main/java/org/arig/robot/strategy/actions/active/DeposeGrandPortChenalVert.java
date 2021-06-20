package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IEurobotConfig;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.ETeam;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DeposeGrandPortChenalVert extends AbstractDeposeGrandPortChenal {

    @Override
    public String name() {
        return IEurobotConfig.ACTION_DEPOSE_GRAND_PORT_VERT;
    }

    @Override
    protected int getBoueeBloquante() {
        if (rs.team() == ETeam.JAUNE) {
            return 16;
        } else {
            return 1;
        }
    }

    @Override
    protected ECouleurBouee getCouleurChenal() {
        return ECouleurBouee.VERT;
    }

    @Override
    protected EPosition getPositionChenal() {
        if (rs.team() == ETeam.BLEU) {
            return EPosition.NORD;
        } else {
            return EPosition.SUD;
        }
    }

}
