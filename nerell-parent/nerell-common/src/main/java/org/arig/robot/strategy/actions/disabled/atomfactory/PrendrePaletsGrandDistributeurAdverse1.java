package org.arig.robot.strategy.actions.disabled.atomfactory;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.CouleurPalet;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class PrendrePaletsGrandDistributeurAdverse1 extends AbstractPrendrePaletsGrandDistributeur {

    public PrendrePaletsGrandDistributeurAdverse1() {
        super(
                // TODO
                new Point(750, 800),
                new Point(2250, 800),
                5,
                4
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
