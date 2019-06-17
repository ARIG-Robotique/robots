package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.enums.CouleurPalet;
import org.arig.robot.strategy.actions.AbstractPrendreGrandDistributeur;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class PrendreGrandDistributeurEquipeBalance extends AbstractPrendreGrandDistributeur {

    public PrendreGrandDistributeurEquipeBalance() {
        super(
                2050,
                950,
                4,
                5,
                98,
                false
        );
    }

    @Override
    protected Map<Integer, CouleurPalet> liste() {
        return rs.getPaletsGrandDistributeurEquipe();
    }

    @Override
    public String name() {
        return "Prise des palets dans le distributeur balance de l'équipe";
    }

}
