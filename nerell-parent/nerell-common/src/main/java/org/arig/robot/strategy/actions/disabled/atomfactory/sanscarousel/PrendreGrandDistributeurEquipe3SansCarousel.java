package org.arig.robot.strategy.actions.disabled.atomfactory.sanscarousel;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.enums.CouleurPalet;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class PrendreGrandDistributeurEquipe3SansCarousel extends AbstractPrendreGrandDistributeur {

    public PrendreGrandDistributeurEquipe3SansCarousel() {
        super(
                2050,
                950,
                4,
                5,
                1
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
