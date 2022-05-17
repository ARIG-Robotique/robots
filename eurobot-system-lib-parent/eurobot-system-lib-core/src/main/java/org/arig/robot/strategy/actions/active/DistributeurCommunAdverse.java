package org.arig.robot.strategy.actions.active;

import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.model.Point;
import org.arig.robot.model.Strategy;
import org.arig.robot.model.Team;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class DistributeurCommunAdverse extends AbstractDistributeurCommun {

    @Override
    public String name() {
        return EurobotConfig.ACTION_PRISE_DISTRIB_COMMUN_ADVERSE;
    }

    @Override
    public List<String> blockingActions() {
        return Collections.singletonList(EurobotConfig.ACTION_PRISE_DISTRIB_COMMUN_EQUIPE);
    }

    @Override
    public boolean isValid() {
        if (rs.strategy() == Strategy.BASIC){
            return false;
        }

        return super.isValid();
    }

    @Override
    public void refreshCompleted() {
        if (rs.strategy() == Strategy.BASIC){
            complete();
            return;
        }
        super.refreshCompleted();
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
