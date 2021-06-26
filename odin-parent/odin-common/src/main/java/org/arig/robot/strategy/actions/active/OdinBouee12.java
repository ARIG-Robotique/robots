package org.arig.robot.strategy.actions.active;

import org.arig.robot.model.ETeam;
import org.arig.robot.model.Point;
import org.springframework.stereotype.Component;

@Component
public class OdinBouee12 extends AbstractOdinBoueeBordure {

    public OdinBouee12() {
        super(12);
    }

    @Override
    protected Point beforeEntry() {
        if (rs.team() == ETeam.BLEU) {
            return new Point(2290, 1500);
        }
        return null;
    }
}
