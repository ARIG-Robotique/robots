package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.services.IPincesAvantService;
import org.arig.robot.strategy.actions.AbstractNerellAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * TODO depose arriere ?
 */
@Slf4j
@Component
public class DeposeGrandPort extends AbstractNerellAction {

    @Autowired
    private IPincesAvantService pincesAvantService;

    private int step = 0;

    @Override
    public String name() {
        return "Dépose grand port";
    }

    @Override
    public Point entryPoint() {
        double X = 460;
        double Y = 1200;
        if (ETeam.JAUNE == rs.getTeam()) {
            X = 3000 - X;
        }
        return new Point(X, Y);
    }

    @Override
    public boolean isValid() {
        return isTimeValid() && !rs.inPort() && !rs.pincesAvantEmpty() && !rs.grandChenaux().chenalRougeEmpty() && !rs.grandChenaux().chenalVertEmpty();
    }

    @Override
    public int order() {
        int order = 0;
        for (ECouleurBouee eCouleurBouee : rs.pincesAvant()) {
            if (eCouleurBouee != null) {
                order++;
            }
        }
        return order + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public void execute() {
        try {
            final Point entry = entryPoint();
            final double y = entry.getY();
            final double x = 210 + step * 80;

            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);
            mv.pathTo(entry, GotoOption.AVANT);

            mv.setVitesse(IConstantesNerellConfig.vitesseSuperLente, IConstantesNerellConfig.vitesseOrientation);

            if (rs.getTeam() == ETeam.BLEU) {
                mv.gotoOrientationDeg(180);
                mv.gotoPoint(x, y);

            } else {
                mv.gotoOrientationDeg(0);
                mv.gotoPoint(3000-x, y);
            }

            pincesAvantService.deposeGrandPort();
            step++;

            mv.gotoPoint(entry, GotoOption.ARRIERE);

            if (step > 2) {
                complete();
            }

        } catch (NoPathFoundException | AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
        } finally {
            pincesAvantService.finaliseDepose();
        }
    }
}
