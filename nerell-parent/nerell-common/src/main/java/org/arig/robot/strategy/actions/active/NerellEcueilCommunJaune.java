package org.arig.robot.strategy.actions.active;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IEurobotConfig;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.EStrategy;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.Point;
import org.arig.robot.services.INerellPincesArriereService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class NerellEcueilCommunJaune extends AbstractNerellEcueil {

    @Autowired
    private NerellBouee10 bouee10;

    @Autowired
    private NerellBouee11 bouee11;

    @Autowired
    private NerellBouee12 bouee12;

    @Override
    public String name() {
        return IEurobotConfig.ACTION_ECUEIL_COMMUN_JAUNE;
    }

    @Getter
    @Accessors(fluent = true)
    public List<String> blockingActions = Arrays.asList(
            IEurobotConfig.ACTION_PRISE_BOUEE_PREFIX + "11",
            IEurobotConfig.ACTION_PRISE_BOUEE_PREFIX + "12"
    );

    @Override
    public Rectangle blockingZone() {
        if (rsNerell.team() == ETeam.BLEU) {
            return IEurobotConfig.ZONE_ECUEIL_COMMUN_ADVERSE_BLEU;
        }
        return null;
    }

    @Override
    public Point entryPoint() {
        double x = 2150;
        double y = 1770;
        return new Point(x, y);
    }

    @Override
    public Point aggressiveIntermediaryPoint()  {
        if (rsNerell.team() == ETeam.JAUNE) {
            // Pas l'eceuil adverse
            return null;
        }
        return new Point(1270, 1460);
    }

    @Override
    public int order() {
        if (rsNerell.team() == ETeam.BLEU && rsNerell.strategy() == EStrategy.AGGRESSIVE && isFirstExecution()) {
            return 1000;
        }
        return super.order();
    }

    @Override
    public boolean isValid() {
        if (rsNerell.team() == ETeam.JAUNE) {
            return super.isValid() && !rsNerell.boueePresente(11) && !rsNerell.boueePresente(12);
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
        return rsNerell.ecueilCommunJauneDispo();
    }

    @Override
    protected INerellPincesArriereService.EEcueil ecueil() {
        return INerellPincesArriereService.EEcueil.JAUNE;
    }

    @Override
    protected ECouleurBouee[] bouees() {
        return rsNerell.team() == ETeam.JAUNE ? rsNerell.couleursEcueilCommunEquipe() : rsNerell.couleursEcueilCommunAdverse();
    }

    @Override
    protected void onAgressiveMvtDone() {
        group.boueePrise(11);
    }

    @Override
    public void execute() {
        if (rsNerell.strategy() != EStrategy.AGGRESSIVE && bouee11.isValid()) {
            bouee11.execute();
        }
        if (bouee12.isValid()) {
            bouee12.execute();
        }
        super.execute();
        if (isCompleted() && bouee10.isValid()) {
            bouee10.execute();
        }
    }

    @Override
    protected void onComplete() {
        // on a shooté la bouée
        group.boueePrise(12);
        rsNerell.ecueilCommunJauneDispo((byte) 0);

        if (rsNerell.team() == ETeam.JAUNE) {
            rsNerell.ecueilCommunEquipePris(true);
        } else {
            rsNerell.ecueilCommunAdversePris(true);
        }
    }
}
