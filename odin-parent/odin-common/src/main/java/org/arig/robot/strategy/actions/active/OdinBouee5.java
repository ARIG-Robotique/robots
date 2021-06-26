package org.arig.robot.strategy.actions.active;

import org.arig.robot.model.ETeam;
import org.arig.robot.model.Point;
import org.springframework.stereotype.Component;

@Component
public class OdinBouee5 extends AbstractOdinBoueeBordure {

    public OdinBouee5() {
        super(5);
    }

    @Override
    protected Point beforeEntry() {
        if (rs.team() == ETeam.JAUNE) {
            return new Point(710, 1500);
        }
        return null;
    }
}
