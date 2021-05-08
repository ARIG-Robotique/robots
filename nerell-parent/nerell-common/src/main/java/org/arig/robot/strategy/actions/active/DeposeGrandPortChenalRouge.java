package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.ETeam;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DeposeGrandPortChenalRouge extends AbstractDeposeGrandPortChenal {

    @Override
    public String name() {
        return "Dépose grand port chenal rouge";
    }

    @Override
    protected int getBoueeBloquante() {
        if (rs.team() == ETeam.BLEU) {
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
        if (rs.team() == ETeam.BLEU) {
            return EPosition.SUD;
        } else {
            return EPosition.NORD;
        }
    }

}
