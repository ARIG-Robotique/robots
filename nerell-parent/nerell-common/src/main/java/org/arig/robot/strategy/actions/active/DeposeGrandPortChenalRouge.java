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
public class DeposeGrandPortChenalRouge extends AbstractDeposeGrandPortChenal {

    @Override
    public String name() {
        return "Dépose grand port chenal rouge";
    }

    @Override
    ECouleurBouee getCouleurChenal() {
        return ECouleurBouee.ROUGE;
    }

    @Override
    EPosition getPositionChenal() {
        if (rs.getTeam() == ETeam.BLEU) {
            return EPosition.SUD;
        } else {
            return EPosition.NORD;
        }
    }

    @Override
    Bouee getBoueeAlternateEntry() {
        if (rs.getTeam() == ETeam.BLEU) {
            return rs.bouee(4);
        } else {
            return rs.bouee(13);
        }
    }

    @Override
    Point getPointAlternateEntry() {
        if (rs.getTeam() == ETeam.BLEU) {
            return new Point(EcueilEquipe.ENTRY_X, EcueilEquipe.ENTRY_Y);
        } else {
            return new Point(3000 - Phare.ENTRY_X, Phare.ENTRY_Y);
        }
    }

    @Override
    Chenaux getChenauxFuture() {
        Chenaux chenaux = rs.grandChenaux().with(rs.pincesArriere(), null);
        if (!rs.pincesAvantEmpty() && rs.phare()) {
            chenaux.addRouge(rs.pincesAvant());
        }
        return chenaux;
    }
}
