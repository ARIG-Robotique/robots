package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.Point;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EcueilEquipe extends AbstractEcueil {

    @Override
    public String name() {
        return "Ecueil equipe";
    }

    @Override
    protected Point entryPoint() {
        double x = 230;
        double y = 400;
        if (ETeam.JAUNE == rs.getTeam()) {
            x = 3000 - x;
        }

        return new Point(x, y);
    }

    @Override
    protected byte nbBoueesDispo() {
        return 5;
    }

    @Override
    protected double orientationPourPrise() {
        return rs.getTeam() == ETeam.BLEU ? 0 : 180;
    }

    @Override
    protected ECouleurBouee[] bouees() {
        return rs.getCouleursEcueilEquipe();
    }
}
