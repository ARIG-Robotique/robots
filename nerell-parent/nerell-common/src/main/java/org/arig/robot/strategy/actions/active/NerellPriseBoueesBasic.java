package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IEurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.model.EStrategy;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.strategy.actions.AbstractNerellAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NerellPriseBoueesBasic extends AbstractNerellAction {

    @Autowired
    private NerellBouee5 bouee5;
    @Autowired
    private NerellBouee6 bouee6;
    @Autowired
    private NerellBouee8 bouee8;
    @Autowired
    private NerellBouee9 bouee9;
    @Autowired
    private NerellBouee11 bouee11;
    @Autowired
    private NerellBouee12 bouee12;

    @Override
    public String name() {
        return IEurobotConfig.ACTION_PRISE_BOUEE_BASIC;
    }

    @Override
    public Point entryPoint() {
        return new Point(getX(470), 1270);
    }

    @Override
    public int order() {
        return rsNerell.strategy() == EStrategy.BASIC ? 1000 : -1000;
    }

    @Override
    public boolean isValid() {
        return rsNerell.strategy() == EStrategy.BASIC;
    }

    @Override
    public void refreshCompleted() {
        if (rsNerell.strategy() != EStrategy.BASIC) {
            complete();
        }
    }

    @Override
    public void execute() {
        try {
            rsNerell.enablePincesAvant();
            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());

            mv.gotoPoint(entryPoint(), GotoOption.SANS_ARRET, GotoOption.SANS_ORIENTATION);

            // On est sorti du port on active évitement
            rsNerell.enableAvoidance();

            // Rush bouée 7 (Bleu) ou 10 (Jaune)
            mv.gotoPoint(getX(1200), 1143, GotoOption.SANS_ORIENTATION);
            group.boueePrise(rsNerell.team() == ETeam.BLEU ? 7 : 10);

            if (rs.team() == ETeam.BLEU) {
                if (bouee8.isValid()) {
                    bouee8.execute();
                }
                if (bouee6.isValid()) {
                    bouee6.execute();
                }
                if (bouee5.isValid()) {
                    bouee5.execute();
                }
            } else {
                if (bouee9.isValid()) {
                    bouee9.execute();
                }
                if (bouee11.isValid()) {
                    bouee11.execute();
                }
                if (bouee12.isValid()) {
                    bouee12.execute();
                }
            }

        } catch (AvoidingException e) {
            log.error("Erreur d'exécution de l'action : {}", e.toString());
        } finally {
            complete();
        }
    }
}
