package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IEurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.model.EStrategy;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.strategy.actions.AbstractNerellAction;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NerellPriseBoueesAggressive extends AbstractNerellAction {

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
            rsNerell.enablePincesAvant(true);
            mv.setVitesse(robotConfig.vitesse(70), robotConfig.vitesseOrientation());

            mv.gotoPoint(entryPoint(), GotoOption.SANS_ARRET, GotoOption.SANS_ORIENTATION);

            // On est sorti du port on active évitement
            rsNerell.enableAvoidance();

            // Rush bouée 7 (Bleu) ou 10 (Jaune)
            mv.gotoPoint(getX(1200), 1143, GotoOption.SANS_ARRET, GotoOption.SANS_ORIENTATION);
            group.boueePrise(rsNerell.team() == ETeam.BLEU ? 7 : 10);

            // Rush bouée 10 (Bleu) ou 7 (Jaune)
            mv.gotoPoint(getX(2030), 1265, GotoOption.SANS_ORIENTATION);
            group.boueePrise(rsNerell.team() == ETeam.BLEU ? 10 : 7);

        } catch (AvoidingException e) {
            log.error("Erreur d'exécution de l'action : {}", e.toString());
        } finally {
            complete();
        }
    }
}
