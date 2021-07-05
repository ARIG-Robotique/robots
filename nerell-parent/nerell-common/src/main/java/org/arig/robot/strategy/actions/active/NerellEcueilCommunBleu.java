package org.arig.robot.strategy.actions.active;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IEurobotConfig;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.EStrategy;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class NerellEcueilCommunBleu extends AbstractNerellEcueil {

    @Autowired
    private NerellBouee5 bouee5;

    @Autowired
    private NerellBouee6 bouee6;

    @Autowired
    private NerellBouee7 bouee7;

    @Override
    public String name() {
        return IEurobotConfig.ACTION_ECUEIL_COMMUN_BLEU;
    }

    @Getter
    @Accessors(fluent = true)
    public List<String> blockingActions = Arrays.asList(
            IEurobotConfig.ACTION_PRISE_BOUEE_PREFIX + "5",
            IEurobotConfig.ACTION_PRISE_BOUEE_PREFIX + "6"
    );

    @Override
    public Rectangle blockingZone() {
        if (rsNerell.team() == ETeam.JAUNE) {
            return IEurobotConfig.ZONE_ECUEIL_COMMUN_ADVERSE_JAUNE;
        }
        return null;
    }

    @Override
    public Point entryPoint() {
        double x = 850;
        double y = 1770;
        return new Point(x, y);
    }

    @Override
    public Point aggressiveIntermediaryPoint() {
        if (rsNerell.team() == ETeam.BLEU) {
            // Pas l'eceuil adverse
            return null;
        }
        return new Point(3000 - 1270, 1460);
    }

    @Override
    public int order() {
        if (rsNerell.team() == ETeam.JAUNE && rsNerell.strategy() == EStrategy.AGGRESSIVE && isFirstExecution()) {
            return 1000;
        }
        return super.order();
    }

    @Override
    public boolean isValid() {
        if (rsNerell.team() == ETeam.BLEU) {
            return super.isValid() && !rsNerell.boueePresente(5) && !rsNerell.boueePresente(6);
        } else {
            return super.isValid() && (rsNerell.strategy() == EStrategy.AGGRESSIVE || rsNerell.getRemainingTime() < 40000);
        }
    }

    @Override
    protected double orientationPourPrise() {
        return -90;
    }

    @Override
    protected byte nbBoueesDispo() {
        return rsNerell.ecueilCommunBleuDispo();
    }

    @Override
    protected ECouleurBouee[] bouees() {
        return rsNerell.team() == ETeam.BLEU ? rsNerell.couleursEcueilCommunEquipe() : rsNerell.couleursEcueilCommunAdverse();
    }

    @Override
    protected void onAgressiveMvtDone() {
        group.boueePrise(6);
    }

    @Override
    public void execute() {
        if (rsNerell.strategy() != EStrategy.AGGRESSIVE && bouee6.isValid()) {
            bouee6.execute();
        }
        if (bouee5.isValid()) {
            bouee5.execute();
        }
        super.execute();
        if (isCompleted() && bouee7.isValid()) {
            bouee7.execute();
        }
    }

    @Override
    protected void onComplete() {
        // on a shooté la bouée
        group.boueePrise(5);
        rsNerell.ecueilCommunBleuDispo((byte) 0);

        if (rsNerell.team() == ETeam.BLEU) {
            group.ecueilCommunEquipePris();
        } else {
            group.ecueilCommunAdversePris();
        }
    }
}
