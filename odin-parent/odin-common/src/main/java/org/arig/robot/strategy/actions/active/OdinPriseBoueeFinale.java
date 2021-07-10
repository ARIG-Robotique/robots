package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IEurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.EStrategy;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.services.AbstractOdinPincesAvantService;
import org.arig.robot.strategy.actions.AbstractOdinAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OdinPriseBoueeFinale extends AbstractOdinAction {

    @Autowired
    private OdinBouee10 bouee10;

    @Autowired
    private OdinBouee7 bouee7;

    @Autowired
    private AbstractOdinPincesAvantService pincesAvant;

    @Override
    public String name() {
        return IEurobotConfig.ACTION_PRISE_BOUEE_AGGRESSIVE + " Odin";
    }

    @Override
    public int order() {
        return 1001;
    }

    @Override
    public boolean isValid() {
        return rs.strategy() == EStrategy.FINALE;
    }

    @Override
    public void refreshCompleted() {
        if (rs.strategy() != EStrategy.FINALE) {
            complete();
        }
    }

    @Override
    public Point entryPoint() {
        return new Point();
    }

    @Override
    public void execute() {
        try {
            rs.enableAvoidance();
            rsOdin.enablePincesAvant();
            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());

            if (rs.team() == ETeam.BLEU) {
                group.boueePrise(8);
                pincesAvant.setExpected(ECouleurBouee.VERT, 1);
            } else {
                group.boueePrise(9);
                pincesAvant.setExpected(ECouleurBouee.ROUGE, 0);
            }
            mv.gotoPoint(getX(1270), 840, GotoOption.SANS_ORIENTATION);

            if (rs.team() == ETeam.BLEU) {
                if (bouee10.isValid()) {
                    bouee10.execute();
                }
                if (bouee7.isValid()) {
                    bouee7.execute();
                }
            } else {
                if (bouee7.isValid()) {
                    bouee7.execute();
                }
                if (bouee10.isValid()) {
                    bouee10.execute();
                }
            }

        } catch (AvoidingException e) {
            log.error("Erreur d'exécution de l'action : {}", e.toString());

        } finally {
            complete();
            pincesAvant.setExpected(null, -1);
        }
    }
}
