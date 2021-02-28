package org.arig.robot.strategy.actions.active;

import org.arig.robot.model.Bouee;
import org.springframework.stereotype.Component;

@Component
public class Bouee7 extends AbstractBouee {

    @Override
    public Bouee bouee() {
        return rs.bouee(7);
    }

}
