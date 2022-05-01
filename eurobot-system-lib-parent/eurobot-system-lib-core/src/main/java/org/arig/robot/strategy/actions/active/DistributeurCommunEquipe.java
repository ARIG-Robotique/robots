package org.arig.robot.strategy.actions.active;

import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.model.Point;
import org.arig.robot.model.Team;
import org.springframework.stereotype.Component;

@Component
public class DistributeurCommunEquipe extends AbstractPriseDistributeurCommun {

    @Override
    public String name() {
        return EurobotConfig.ACTION_PRISE_DISTRIB_COMMUN_EQUIPE;
    }

    @Override
    public int order() {
        return super.order() + 3; // 3 points par prise
    }

    @Override
    protected boolean isDistributeurPris() {
        return rs.distributeurCommunEquipePris();
    }

    @Override
    protected void setDistributeurPris() {
        group.distributeurCommunEquipePris();
    }

    @Override
    public Point entryPoint() {
        return new Point(getX(ENTRY_X), ENTRY_Y);
    }

    @Override
    protected int angleCallageX() {
        return rs.team() == Team.JAUNE ? 0 : 180;
    }

    @Override
    protected int anglePrise() {
        return rs.team() == Team.JAUNE ? 85 : 95;
    }
}
