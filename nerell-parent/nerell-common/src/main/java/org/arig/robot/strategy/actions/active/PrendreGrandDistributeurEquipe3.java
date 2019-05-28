package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.enums.CouleurPalet;
import org.arig.robot.strategy.actions.AbstractPrendreGrandDistributeur;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class PrendreGrandDistributeurEquipe3 extends AbstractPrendreGrandDistributeur {

    public PrendreGrandDistributeurEquipe3() {
        super(
                2050,
                950,
                4,
                5,
                3
        );
    }

    @Override
    protected Map<Integer, CouleurPalet> liste() {
        return rs.getPaletsGrandDistributeurEquipe();
    }

    @Override
    public String name() {
        return "Prise des palets dans le grand distributeur de l'équipe";
    }

}
