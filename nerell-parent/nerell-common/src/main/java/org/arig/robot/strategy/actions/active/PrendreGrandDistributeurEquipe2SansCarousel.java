package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.enums.CouleurPalet;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class PrendreGrandDistributeurEquipe2SansCarousel extends AbstractPrendreGrandDistributeur {

    public PrendreGrandDistributeurEquipe2SansCarousel() {
        super(
                2250,
                750,
                2,
                3,
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

}
