package org.arig.robot.strategy.actions.active;

import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.model.Point;
import org.arig.robot.model.Team;
import org.springframework.stereotype.Component;

@Component
public class DistributeurCommunAdverse extends AbstractPriseDistributeurCommun {

    @Override
    public String name() {
        return EurobotConfig.ACTION_PRISE_DISTRIB_COMMUN_ADVERSE;
    }

    @Override
    protected boolean isDistributeurPris() {
        return rs.distributeurCommunAdversePris();
    }

    @Override
    protected boolean isDistributeurBloque() {
        return rs.distributeurCommunAdverseBloque();
    }

    @Override
    protected void setDistributeurPris() {
        group.distributeurCommunAdversePris();
    }

    @Override
    protected void setDistributeurBloque() {
        group.distributeurCommunAdverseBloque();
    }

    @Override
    public Point entryPoint() {
        return new Point(3000 - getX(ENTRY_X), ENTRY_Y);
    }

    @Override
    protected int angleCallageX() {
        return rs.team() == Team.JAUNE ? 180 : 0;
    }

    @Override
    protected int anglePrise() {
        return rs.team() == Team.JAUNE ? 95 : 85;
    }
}
