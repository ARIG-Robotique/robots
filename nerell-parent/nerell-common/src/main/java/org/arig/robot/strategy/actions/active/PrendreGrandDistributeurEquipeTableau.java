package org.arig.robot.strategy.actions.disabled.atomfactory;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.enums.CouleurPalet;
import org.arig.robot.services.MagasinService;
import org.arig.robot.strategy.actions.AbstractPrendreGrandDistributeur;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class PrendreGrandDistributeurEquipeTableau extends AbstractPrendreGrandDistributeur {

    @Autowired
    private MagasinService magasin;

    public PrendreGrandDistributeurEquipeTableau() {
        super(
                2450,
                550,
                0,
                1,
                99,
                false
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

    @Override
    public void execute() {
        super.execute(false);

        magasin.moisson();
    }
}
