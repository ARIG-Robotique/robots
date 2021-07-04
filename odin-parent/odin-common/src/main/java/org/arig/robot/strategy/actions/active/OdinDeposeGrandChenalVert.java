package org.arig.robot.strategy.actions.active;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IEurobotConfig;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.GrandChenaux;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class OdinDeposeGrandChenalVert extends AbstractOdinDeposeGrandChenal {

    @Override
    public String name() {
        return IEurobotConfig.ACTION_DEPOSE_GRAND_PORT_VERT;
    }

    @Getter
    @Accessors(fluent = true)
    public List<String> blockingActions = Arrays.asList(
            IEurobotConfig.ACTION_DEPOSE_GRAND_PORT,
            IEurobotConfig.ACTION_DEPOSE_GRAND_PORT_ROUGE
    );

    @Override
    public Rectangle blockingZone() {
        return rs.team() == ETeam.BLEU ? IEurobotConfig.ZONE_GRAND_PORT_BLEU : IEurobotConfig.ZONE_GRAND_PORT_JAUNE;
    }

    @Override
    protected ECouleurBouee getCouleurChenal() {
        return ECouleurBouee.VERT;
    }

    @Override
    protected EPosition getPositionChenal() {
        if (rs.team() == ETeam.BLEU) {
            return EPosition.NORD;
        } else {
            return EPosition.SUD;
        }
    }

    @Override
    protected ECouleurBouee[] getPinces() {
        return rsOdin.pincesAvant();
    }

    @Override
    protected List<ECouleurBouee> getChenal(GrandChenaux.Line line) {
        return rs.getGrandChenalVert(line);
    }
}