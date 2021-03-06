package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.EStrategy;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EcueilCommunJaune extends AbstractEcueil {

    @Autowired
    private Bouee11 bouee11;

    @Autowired
    private Bouee10 bouee10;

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
        if (rs.team() == ETeam.JAUNE) {
            // Pas l'eceuil adverse
            return null;
        }
        return new Point(1270, 1460);
    }

    @Override
    public int order() {
        if (rs.team() == ETeam.BLEU && rs.strategy() == EStrategy.AGGRESSIVE && isFirstExecution()) {
            return 1000;
        }
        return super.order();
    }

    @Override
    public boolean isValid() {
        if (rs.team() == ETeam.JAUNE) {
            return super.isValid() && !rs.bouee(11).presente() && !rs.bouee(12).presente();
        } else {
            return super.isValid() && (rs.strategy() == EStrategy.AGGRESSIVE || rs.getRemainingTime() < 40000);
        }
    }

    @Override
    protected double orientationPourPrise() {
        return -90;
    }

    @Override
    protected byte nbBoueesDispo() {
        return rs.ecueilCommunJauneDispo();
    }

    @Override
    protected ECouleurBouee[] bouees() {
        return rs.team() == ETeam.JAUNE ? rs.couleursEcueilCommunEquipe() : rs.couleursEcueilCommunAdverse();
    }

    @Override
    protected void onAgressiveMvtDone() {
        rs.bouee(11).setPrise();
    }

    @Override
    public void execute() {
        if (rs.strategy() != EStrategy.AGGRESSIVE && bouee11.isValid()) {
            bouee11.execute();
        }
        super.execute();
        if (isCompleted() && bouee11.isValid()) {
            bouee11.execute();
        }
    }

    @Override
    protected void onComplete() {
        // on shooté la bouée
        rs.bouee(12).setPrise();
        rs.ecueilCommunJauneDispo((byte) 0);

        if (rs.team() == ETeam.JAUNE) {
            rs.ecueilCommunEquipePris(true);
        } else {
            rs.ecueilCommunAdversePris(true);
        }
    }
}
