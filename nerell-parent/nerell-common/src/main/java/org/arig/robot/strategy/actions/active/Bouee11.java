package org.arig.robot.strategy.actions.active;

import org.arig.robot.model.Bouee;
import org.springframework.stereotype.Component;

@Component
public class Bouee11 extends AbstractBouee {

    @Override
    public Bouee bouee() {
        return rs.bouee(11);
    }

}
