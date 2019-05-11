package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.enums.CouleurPalet;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class PrendreGrandDistributeurEquipe1SansCarousel extends AbstractPrendreGrandDistributeur {

    public PrendreGrandDistributeurEquipe1SansCarousel() {
        super(
                2450,
                550,
                0,
                1,
                3
        );
    }

    @Override
    public Map<Integer, CouleurPalet> liste() {
        return rs.getPaletsGrandDistributeurEquipe();
    }

    @Override
    public String name() {
        return "Prise des palets dans le grand distributeur de l'équipe";
    }

}
