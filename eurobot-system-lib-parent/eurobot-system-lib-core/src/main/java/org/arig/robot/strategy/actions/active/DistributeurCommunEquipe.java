package org.arig.robot.strategy.actions.active;

import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.model.Point;
import org.arig.robot.model.Team;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class DistributeurCommunEquipe extends AbstractPriseDistributeurCommun {

    @Override
    public String name() {
        return EurobotConfig.ACTION_PRISE_DISTRIB_COMMUN_EQUIPE;
    }

    @Override
    public List<String> blockingActions() {
        return Collections.singletonList(EurobotConfig.ACTION_PRISE_DISTRIB_COMMUN_ADVERSE);
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
    protected boolean isDistributeurBloque() {
        return rs.distributeurCommunEquipeBloque();
    }

    @Override
    protected void setDistributeurPris() {
        group.distributeurCommunEquipePris();
    }

    @Override
    protected void setDistributeurBloque() {
        group.distributeurCommunEquipeBloque();
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
