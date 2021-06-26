package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.ENerellStrategy;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
        return "Ecueil commun jaune";
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
        if (rsNerell.team() == ETeam.BLEU && rsNerell.strategy() == ENerellStrategy.AGGRESSIVE && isFirstExecution()) {
            return 1000;
        }
        return super.order();
    }

    @Override
    public boolean isValid() {
        if (rsNerell.team() == ETeam.JAUNE) {
            return super.isValid() && !rsNerell.boueePresente(11) && !rsNerell.boueePresente(12);
        } else {
            return super.isValid() && (rsNerell.strategy() == ENerellStrategy.AGGRESSIVE || rsNerell.getRemainingTime() < 40000);
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
    protected ECouleurBouee[] bouees() {
        return rsNerell.team() == ETeam.JAUNE ? rsNerell.couleursEcueilCommunEquipe() : rsNerell.couleursEcueilCommunAdverse();
    }

    @Override
    protected void onAgressiveMvtDone() {
        group.boueePrise(11);
    }

    @Override
    public void execute() {
        if (rsNerell.strategy() != ENerellStrategy.AGGRESSIVE && bouee11.isValid()) {
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
