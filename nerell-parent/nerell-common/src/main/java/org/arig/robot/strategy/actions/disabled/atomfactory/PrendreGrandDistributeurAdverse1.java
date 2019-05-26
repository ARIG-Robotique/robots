package org.arig.robot.strategy.actions.disabled.atomfactory;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.enums.CouleurPalet;
import org.arig.robot.strategy.actions.AbstractPrendreGrandDistributeur;
import org.springframework.stereotype.Component;

import java.util.Map;

import static org.arig.robot.model.EStrategy.PRISE_ADVERSE;

@Slf4j
@Component
public class PrendreGrandDistributeurAdverse1 extends AbstractPrendreGrandDistributeur {

    public PrendreGrandDistributeurAdverse1() {
        super(
                950,
                2050,
                5,
                4,
                1
        );
    }

    @Override
    protected Map<Integer, CouleurPalet> liste() {
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
