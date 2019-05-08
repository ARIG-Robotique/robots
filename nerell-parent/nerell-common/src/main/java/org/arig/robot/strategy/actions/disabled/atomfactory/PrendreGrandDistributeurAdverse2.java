package org.arig.robot.strategy.actions.disabled.atomfactory;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.enums.CouleurPalet;
import org.springframework.stereotype.Component;

import java.util.Map;

import static org.arig.robot.model.EStrategy.PRISE_ADVERSE;

@Slf4j
@Component
public class PrendreGrandDistributeurAdverse2 extends AbstractPrendreGrandDistributeur {

    public PrendreGrandDistributeurAdverse2() {
        super(
                750,
                2250,
                3,
                2,
                1
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
    public boolean isValid() {
        return rs.strategyActive(PRISE_ADVERSE) && super.isValid();
    }

}
