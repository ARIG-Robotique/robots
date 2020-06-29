package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.EStrategy;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.Point;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EcueilCommunJaune extends AbstractEcueil {

    private boolean firstExecution = true;

    @Override
    public String name() {
        return "Ecueil commun jaune";
    }

    @Override
    protected Point entryPoint() {
        double x = 2150;
        double y = 1770;
        return new Point(x, y);
    }

    @Override
    public int order() {
        if (rs.getTeam() == ETeam.BLEU && rs.getStrategy() == EStrategy.AGGRESSIVE && firstExecution) {
            return 1000;
        }
        return super.order();
    }

    @Override
    public boolean isValid() {
        if (rs.getTeam() == ETeam.JAUNE) {
            return super.isValid() && rs.bouee(11).prise() && rs.bouee(12).prise();
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
        return rs.getEcueilCommunJauneDispo();
    }

    @Override
    protected ECouleurBouee[] bouees() {
        return rs.getTeam() == ETeam.JAUNE ? rs.getCouleursEcueilCommunEquipe() : rs.getCouleursEcueilCommunAdverse();
    }

    @Override
    protected void onStart() {
        firstExecution = false;
    }

    @Override
    protected void onComplete() {
        // on shooté la bouée
        rs.bouee(12).prise(true);

        if (rs.getTeam() == ETeam.JAUNE) {
            rs.setEcueilCommunEquipePris(true);
        } else {
            rs.setEcueilCommunAdversePris(true);
        }
    }
}
