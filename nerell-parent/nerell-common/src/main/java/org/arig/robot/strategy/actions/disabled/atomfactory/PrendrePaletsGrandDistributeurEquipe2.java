package org.arig.robot.strategy.actions.disabled.atomfactory;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.CouleurPalet;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class PrendrePaletsGrandDistributeurEquipe2 extends AbstractPrendrePaletsGrandDistributeur {

    public PrendrePaletsGrandDistributeurEquipe2() {
        super(
                // TODO
                new Point(2350, 800),
                new Point(650, 800),
                2,
                3
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
