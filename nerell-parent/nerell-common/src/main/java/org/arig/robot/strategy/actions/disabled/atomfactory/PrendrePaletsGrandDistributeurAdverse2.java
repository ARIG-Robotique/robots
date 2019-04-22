package org.arig.robot.strategy.actions.disabled.atomfactory;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.CouleurPalet;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class PrendrePaletsGrandDistributeurAdverse2 extends AbstractPrendrePaletsGrandDistributeur {

    public PrendrePaletsGrandDistributeurAdverse2() {
        super(
                // TODO
                new Point(650, 800),
                new Point(2350, 800),
                3,
                2
        );
    }

    @Override
    Map<Integer, CouleurPalet> liste() {
        return rs.getPaletsGrandDistributeurAdverse();
    }

    @Override
    public String name() {
        return "Prise des palets dans le grand distributeur adverse";
    }

    @Override
    public int order() {
        return 0; // TODO
    }

}
