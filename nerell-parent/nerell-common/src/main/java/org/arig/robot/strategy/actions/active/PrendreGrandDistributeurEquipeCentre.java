package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.enums.CouleurPalet;
import org.arig.robot.strategy.actions.AbstractPrendreGrandDistributeur;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class PrendreGrandDistributeurEquipeCentre extends AbstractPrendreGrandDistributeur {

    public PrendreGrandDistributeurEquipeCentre() {
        super(
                2250,
                750,
                2,
                3,
                100,
                true
        );
    }

    @Override
    protected Map<Integer, CouleurPalet> liste() {
        return rs.getPaletsGrandDistributeurEquipe();
    }

    @Override
    public String name() {
        return "Prise des palets dans le distributeur centre de l'équipe";
    }

}
