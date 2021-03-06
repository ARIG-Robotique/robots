package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.Bouee;
import org.arig.robot.model.Chenaux;
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
    protected Bouee getBoueeBloquante() {
        if (rs.team() == ETeam.BLEU) {
            return rs.bouee(4);
        } else {
            return rs.bouee(13);
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

    @Override
    protected Chenaux getChenauxFuture() {
        Chenaux chenaux = rs.grandChenaux().with(null, null);
        if (!rs.pincesArriereEmpty()) {
            chenaux.addRouge(rs.pincesArriere());
        }
        if (!rs.pincesAvantEmpty() && (rs.pincesArriereEmpty() || rs.doubleDepose())) {
            chenaux.addRouge(rs.pincesAvant());
        }
        return chenaux;
    }
}
