package org.arig.robot.strategy.actions.active;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IEurobotConfig;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.GrandChenaux;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class OdinDeposeGrandChenalRouge extends AbstractOdinDeposeGrandChenal {

    @Override
    public String name() {
        return IEurobotConfig.ACTION_DEPOSE_GRAND_PORT_ROUGE;
    }

    @Getter
    @Accessors(fluent = true)
    public List<String> blockingActions = Arrays.asList(
            IEurobotConfig.ACTION_DEPOSE_GRAND_PORT,
            IEurobotConfig.ACTION_DEPOSE_GRAND_PORT_VERT
    );

    @Override
    protected ECouleurBouee getCouleurChenal() {
        return ECouleurBouee.ROUGE;
    }

    @Override
    protected EPosition getPositionChenal() {
        if (rs.team() == ETeam.BLEU) {
            return EPosition.SUD;
        } else {
            return EPosition.NORD;
        }
    }

    @Override
    protected List<ECouleurBouee> getChenal(GrandChenaux.Line line) {
        return rs.getGrandChenalRouge(line);
    }
}
