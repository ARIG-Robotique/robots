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
public class EcueilCommunBleu extends AbstractEcueil {

    @Autowired
    private Bouee6 bouee6;

    @Autowired
    private Bouee7 bouee7;

    @Override
    public String name() {
        return "Ecueil commun bleu";
    }

    @Override
    public Point entryPoint() {
        double x = 850;
        double y = 1770;
        return new Point(x, y);
    }

    @Override
    public Point aggressiveIntermediaryPoint() {
        return new Point(3000 - 1270, 1460);
    }

    @Override
    public int order() {
        if (rs.getTeam() == ETeam.JAUNE && rs.getStrategy() == EStrategy.AGGRESSIVE && isFirstExecution()) {
            return 1000;
        }
        return super.order();
    }

    @Override
    public boolean isValid() {
        if (rs.getTeam() == ETeam.BLEU) {
            return super.isValid() && !rs.bouee(5).presente() && !rs.bouee(6).presente();
        } else {
            return super.isValid() && (rs.getStrategy() == EStrategy.AGGRESSIVE || rs.getRemainingTime() < 40000);
        }
    }

    @Override
    protected double orientationPourPrise() {
        return -90;
    }

    @Override
    protected byte nbBoueesDispo() {
        return rs.getEcueilCommunBleuDispo();
    }

    @Override
    protected ECouleurBouee[] bouees() {
        return rs.getTeam() == ETeam.BLEU ? rs.getCouleursEcueilCommunEquipe() : rs.getCouleursEcueilCommunAdverse();
    }

    @Override
    protected void onAgressiveMvtDone() {
        rs.bouee(6).setPrise();
    }

    @Override
    public void execute() {
        if (rs.getStrategy() != EStrategy.AGGRESSIVE && bouee6.isValid()) {
            bouee6.execute();
        }
        super.execute();
        if (isCompleted() && bouee7.isValid()) {
            bouee7.execute();
        }
    }

    @Override
    protected void onComplete() {
        // on shooté la bouée
        rs.bouee(5).setPrise();
        rs.setEcueilCommunBleuDispo((byte) 0);

        if (rs.getTeam() == ETeam.BLEU) {
            rs.setEcueilCommunEquipePris(true);
        } else {
            rs.setEcueilCommunAdversePris(true);
        }
    }
}
