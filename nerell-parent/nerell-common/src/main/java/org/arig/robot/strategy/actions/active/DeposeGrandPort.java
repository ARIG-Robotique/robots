package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.services.IPincesArriereService;
import org.arig.robot.services.IPincesAvantService;
import org.arig.robot.strategy.actions.AbstractNerellAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DeposeGrandPort extends AbstractNerellAction {

    @Autowired
    private IPincesAvantService pincesAvantService;

    @Autowired
    private IPincesArriereService pincesArriereService;

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
        return isTimeValid() && !rs.inPort() && (!rs.pincesAvantEmpty() || !rs.pincesArriereEmpty())
                && !rs.grandChenaux().chenalRougeEmpty() && !rs.grandChenaux().chenalVertEmpty();
    }

    @Override
    public int order() {
        int order = 0;
        for (ECouleurBouee eCouleurBouee : rs.pincesAvant()) {
            if (eCouleurBouee != null) {
                order++;
            }
        }
        for (ECouleurBouee eCouleurBouee : rs.pincesArriere()) {
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

            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);
            mv.pathTo(entry);

            mv.setVitesse(IConstantesNerellConfig.vitesseSuperLente, IConstantesNerellConfig.vitesseOrientation);

            do {
                double x = offsetX();
                if (!rs.pincesAvantEmpty()) {
                    if (rs.getTeam() == ETeam.BLEU) {
                        mv.gotoPoint(x, y);
                        mv.gotoOrientationDeg(180);
                    } else {
                        mv.gotoPoint(3000 - x, y);
                        mv.gotoOrientationDeg(0);
                    }
                    pincesAvantService.deposeGrandPort();
                    step++;
                }
                x = offsetX();
                if (!rs.pincesArriereEmpty()) {
                    if (rs.getTeam() == ETeam.BLEU) {
                        mv.gotoPoint(x, y);
                        mv.gotoOrientationDeg(0);
                    } else {
                        mv.gotoPoint(3000 - x, y);
                        mv.gotoOrientationDeg(180);
                    }
                    pincesArriereService.deposeGrandPort();
                    step++;
                }
                mv.gotoPoint(entry);
            } while (!rs.pincesAvantEmpty() && !rs.pincesArriereEmpty());

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

    private int offsetX() {
        return 210 + step * 120;
    }
}
