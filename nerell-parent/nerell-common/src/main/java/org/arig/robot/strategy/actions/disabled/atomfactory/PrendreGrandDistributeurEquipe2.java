package org.arig.robot.strategy.actions.disabled.atomfactory;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.enums.CouleurPalet;
import org.arig.robot.strategy.actions.AbstractPrendreGrandDistributeur;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class PrendreGrandDistributeurEquipe2 extends AbstractPrendreGrandDistributeur {

    public PrendreGrandDistributeurEquipe2() {
        super(
                2250,
                750,
                2,
                3,
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
