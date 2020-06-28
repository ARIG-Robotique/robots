package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.Bouee;
import org.arig.robot.model.Chenaux;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.Point;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DeposeGrandPortChenalVert extends AbstractDeposeGrandPortChenal {

    @Override
    public String name() {
        return "Dépose grand port chenal vert";
    }

    @Override
    protected ECouleurBouee getCouleurChenal() {
        return ECouleurBouee.VERT;
    }

    @Override
    protected EPosition getPositionChenal() {
        if (rs.getTeam() == ETeam.BLEU) {
            return EPosition.NORD;
        } else {
            return EPosition.SUD;
        }
    }

    @Override
    protected Bouee getBoueeAlternateEntry() {
        if (rs.getTeam() == ETeam.BLEU) {
            return rs.bouee(1);
        } else {
            return rs.bouee(16);
        }
    }

    @Override
    protected Point getPointAlternateEntry() {
        if (rs.getTeam() == ETeam.BLEU) {
            return new Point(Phare.ENTRY_X, Phare.ENTRY_Y);
        } else {
            return new Point(3000 - EcueilEquipe.ENTRY_X, EcueilEquipe.ENTRY_Y);
        }
    }

    @Override
    protected Chenaux getChenauxFuture() {
        Chenaux chenaux = rs.grandChenaux().with(null, rs.pincesArriere());
        if (!rs.pincesAvantEmpty() && rs.phare()) {
            chenaux.addVert(rs.pincesAvant());
        }
        return chenaux;
    }
}
