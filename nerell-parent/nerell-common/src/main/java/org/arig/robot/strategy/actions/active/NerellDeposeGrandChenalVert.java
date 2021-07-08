package org.arig.robot.strategy.actions.active;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IEurobotConfig;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.ETeam;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class NerellDeposeGrandChenalVert extends AbstractNerellDeposeGrandChenal {

    @Override
    public String name() {
        return IEurobotConfig.ACTION_DEPOSE_GRAND_PORT_VERT;
    }

    @Getter
    @Accessors(fluent = true)
    public List<String> blockingActions = Arrays.asList(
            IEurobotConfig.ACTION_DEPOSE_GRAND_PORT,
            IEurobotConfig.ACTION_DEPOSE_GRAND_PORT_ROUGE,
            IEurobotConfig.ACTION_NETTOYAGE_GRAND_PORT
    );

    @Override
    protected int getBoueeBloquante() {
        if (rsNerell.team() == ETeam.JAUNE) {
            return 16;
        } else {
            return 1;
        }
    }

    @Override
    protected ECouleurBouee getCouleurChenal() {
        return ECouleurBouee.VERT;
    }

    @Override
    protected EPosition getPositionChenal() {
        if (rsNerell.team() == ETeam.BLEU) {
            return EPosition.NORD;
        } else {
            return EPosition.SUD;
        }
    }

    @Override
    protected double getTweakY() {
        if (rs.team() == ETeam.BLEU) {
            return 100;
        } else {
            return -100;
        }
    }
}
