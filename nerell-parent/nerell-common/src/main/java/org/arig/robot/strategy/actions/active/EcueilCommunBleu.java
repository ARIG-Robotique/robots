package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.Point;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EcueilCommunBleu extends AbstractEcueil {

    @Override
    public String name() {
        return "Ecueil commun bleu";
    }

    @Override
    protected Point entryPoint() {
        double x = 850;
        double y = 1770;
        return new Point(x, y);
    }

    @Override
    public boolean isValid() {
        if (rs.getTeam() == ETeam.BLEU) {
            return super.isValid() && rs.bouee(5).prise() && rs.bouee(6).prise();
        } else {
            return super.isValid();
        }
    }

    @Override
    protected double orientationPourPrise() {
        return -90;
    }

    @Override
    protected byte nbBoueesDispo() {
        return rs.getEcueilCommunBleuDispo();
    }

    @Override
    protected ECouleurBouee[] bouees() {
        return rs.getTeam() == ETeam.BLEU ? rs.getCouleursEcueilCommunEquipe() : rs.getCouleursEcueilCommunAdverse();
    }

    @Override
    protected void onComplete() {
        if (rs.getTeam() == ETeam.BLEU) {
            rs.setEcueilCommunEquipePris(true);
        } else {
            rs.setEcueilCommunAdversePris(true);
        }
    }
}
