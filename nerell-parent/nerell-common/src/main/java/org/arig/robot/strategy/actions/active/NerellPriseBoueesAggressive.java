package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IEurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.EStrategy;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.strategy.actions.AbstractNerellAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NerellPriseBoueesAggressive extends AbstractNerellAction {

    @Autowired
    private NerellBouee8 bouee8;

    @Autowired
    private NerellBouee9 bouee9;

    @Override
    public String name() {
        return IEurobotConfig.ACTION_PRISE_BOUEE_AGGRESSIVE;
    }

    @Override
    public Point entryPoint() {
        return new Point(getX(470), 1270);
    }

    @Override
    public int order() {
        return rsNerell.strategy() == EStrategy.AGGRESSIVE ? 1000 : -1000;
    }

    @Override
    public boolean isValid() {
        return rsNerell.strategy() == EStrategy.AGGRESSIVE;
    }

    @Override
    public void refreshCompleted() {
        if (rsNerell.strategy() != EStrategy.AGGRESSIVE) {
            complete();
        }
    }

    @Override
    public void execute() {
        try {
            rsNerell.enablePincesAvant();
            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());

            mv.gotoPoint(entryPoint(), GotoOption.SANS_ORIENTATION, GotoOption.SANS_ARRET);

            // On est sorti du port on active évitement
            rsNerell.enableAvoidance();

            // Rush bouée 7 (Bleu) ou 10 (Jaune)
            mv.gotoPoint(getX(1200), 1140, GotoOption.SANS_ORIENTATION, GotoOption.SANS_ARRET);
            rs.boueePrise(rsNerell.team() == ETeam.BLEU ? 7 : 10);

            // Rush bouée 10 (Bleu) ou 7 (Jaune)
            mv.gotoPoint(getX(2030), 1250, GotoOption.SANS_ORIENTATION);
            rs.boueePrise(rsNerell.team() == ETeam.BLEU ? 10 : 7);

            // En cas d'erreur sur bouee 9 ou 8
            complete();

            if (rs.team() == ETeam.BLEU) {
                if (bouee8.isValid()) {
                    bouee8.execute();
                }
                if (bouee9.isValid()) {
                    bouee9.execute();
                }
            } else {
                if (bouee9.isValid()) {
                    bouee9.execute();
                }
                if (bouee8.isValid()) {
                    bouee8.execute();
                }
            }

        } catch (AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'exécution de l'action : {}", e.toString());
        } finally {
            complete();
        }
    }
}
