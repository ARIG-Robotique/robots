package org.arig.robot.strategy.actions.disabled.atomfactory;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.CouleurPalet;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class PrendrePaletsGrandDistributeurEquipe1 extends AbstractPrendrePaletsGrandDistributeur {

    public PrendrePaletsGrandDistributeurEquipe1() {
        super(
                // TODO
                new Point(2450, 800),
                new Point(550, 800),
                0,
                1
        );
    }

    @Override
    Map<Integer, CouleurPalet> liste() {
        return rs.getPaletsGrandDistributeurEquipe();
    }

    @Override
    public String name() {
        return "Prise des palets dans le grand distributeur de l'équipe";
    }

    @Override
    public int order() {
        return 0; // TODO
    }

}
