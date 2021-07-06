package org.arig.robot.strategy.actions.active;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IEurobotConfig;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.Point;
import org.arig.robot.services.INerellPincesArriereService;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class NerellEcueilEquipe extends AbstractNerellEcueil {

    public static final double ENTRY_X = 230;
    public static final double ENTRY_Y = 400;

    @Override
    public String name() {
        return IEurobotConfig.ACTION_ECUEIL_EQUIPE;
    }

    @Getter
    @Accessors(fluent = true)
    public List<String> blockingActions = Arrays.asList(
            IEurobotConfig.ACTION_MANCHE_A_AIR
    );

    @Override
    public Rectangle blockingZone() {
        return rsNerell.team() == ETeam.BLEU ? IEurobotConfig.ZONE_ECUEIL_EQUIPE_BLEU : IEurobotConfig.ZONE_ECUEIL_EQUIPE_JAUNE;
    }

    @Override
    public Point entryPoint() {
        double x = ENTRY_X;
        double y = ENTRY_Y;
        if (ETeam.JAUNE == rsNerell.team()) {
            x = 3000 - x;
        }

        return new Point(x, y);
    }

    @Override
    protected byte nbBoueesDispo() {
        return 5;
    }

    @Override
    protected double orientationPourPrise() {
        return rsNerell.team() == ETeam.BLEU ? 0 : 180;
    }

    @Override
    protected INerellPincesArriereService.EEcueil ecueil() {
        return INerellPincesArriereService.EEcueil.EQUIPE;
    }

    @Override
    protected ECouleurBouee[] bouees() {
        return rsNerell.couleursEcueilEquipe();
    }

    @Override
    protected void onComplete() {
        group.ecueilEquipePris();
    }
}
