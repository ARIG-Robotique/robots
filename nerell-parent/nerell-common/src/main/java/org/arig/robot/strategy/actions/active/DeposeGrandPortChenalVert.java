package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.Bouee;
import org.arig.robot.model.Chenaux;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.ETeam;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DeposeGrandPortChenalVert extends AbstractDeposeGrandPortChenal {

    @Override
    public String name() {
        return "Dépose grand port chenal vert";
    }

    @Override
    protected Bouee getBoueeBloquante() {
        if (rs.team() == ETeam.JAUNE) {
            return rs.bouee(16);
        } else {
            return rs.bouee(1);
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

    @Override
    protected Chenaux getChenauxFuture() {
        Chenaux chenaux = rs.grandChenaux().with(null, null);
        if (!rs.pincesArriereEmpty()) {
            chenaux.addVert(rs.pincesArriere());
        }
        if (!rs.pincesAvantEmpty() && (rs.pincesArriereEmpty() || rs.doubleDepose())) {
            chenaux.addVert(rs.pincesAvant());
        }
        return chenaux;
    }
}
