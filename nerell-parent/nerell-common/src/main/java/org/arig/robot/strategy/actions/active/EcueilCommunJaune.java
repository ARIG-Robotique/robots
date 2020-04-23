package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.Point;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EcueilCommunJaune extends AbstractEcueil {

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
}
