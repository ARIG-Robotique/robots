package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IEurobotConfig;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.ETeam;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NerellDeposeGrandPortChenalRouge extends AbstractNerellDeposeGrandPortChenal {

    @Override
    public String name() {
        return IEurobotConfig.ACTION_DEPOSE_GRAND_PORT_ROUGE;
    }

    @Override
    protected int getBoueeBloquante() {
        if (rsNerell.team() == ETeam.BLEU) {
            return 4;
        } else {
            return 13;
        }
    }

    @Override
    protected ECouleurBouee getCouleurChenal() {
        return ECouleurBouee.ROUGE;
    }

    @Override
    protected EPosition getPositionChenal() {
        if (rsNerell.team() == ETeam.BLEU) {
            return EPosition.SUD;
        } else {
            return EPosition.NORD;
        }
    }

}
