package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.services.INerellPincesArriereService;
import org.arig.robot.services.INerellPincesAvantService;
import org.arig.robot.strategy.actions.AbstractNerellAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DeposeGrandPort extends AbstractNerellAction {

    @Autowired
    private INerellPincesAvantService pincesAvantService;

    @Autowired
    private INerellPincesArriereService pincesArriereService;

    private int step = 0;

    @Override
    public String name() {
        return "Dépose grand port";
    }

    @Override
    public Point entryPoint() {
        double X = 230;
        double Y = 1200;
        if (ETeam.JAUNE == rs.team()) {
            X = 3000 - X;
        }
        return new Point(X, Y);
    }

    @Override
    public boolean isValid() {
        return isTimeValid() && !rs.inPort() && (!rs.pincesAvantEmpty() || !rs.pincesArriereEmpty())
                && !rs.grandChenalRougeEmpty() && !rs.grandChenalVertEmpty();
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
            final Point entry2 = new Point(computeX(entry.getX(), !rs.pincesAvantEmpty()), entry.getY());

            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());

            if (tableUtils.distance(entry2) > 200) {
                mv.pathTo(entry2, !rs.pincesAvantEmpty() ? GotoOption.AVANT : GotoOption.ARRIERE);
                rs.disableAvoidance();
            } else {
                rs.disableAvoidance();
                mv.gotoPoint(entry2, GotoOption.SANS_ORIENTATION, !rs.pincesAvantEmpty() ? GotoOption.AVANT : GotoOption.ARRIERE);
            }

            mv.setVitesse(robotConfig.vitesse(50), robotConfig.vitesseOrientation());

            do {
                if (!rs.pincesAvantEmpty()) {
                    if (rs.team() == ETeam.BLEU) {
                        mv.gotoOrientationDeg(180);
                    } else {
                        mv.gotoOrientationDeg(0);
                    }
                    pincesAvantService.deposeGrandPort();
                    step++;
                }
                if (!rs.pincesArriereEmpty()) {
                    final Point entry3 = new Point(computeX(entry.getX(), false), entry.getY());
                    mv.gotoPoint(entry3, GotoOption.SANS_ORIENTATION);
                    if (rs.team() == ETeam.BLEU) {
                        mv.gotoOrientationDeg(0);
                    } else {
                        mv.gotoOrientationDeg(180);
                    }
                    pincesArriereService.deposeGrandPort();
                    step++;
                }

                mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
                if (rs.team() == ETeam.BLEU) {
                    mv.gotoPoint(500, entry.getY());
                } else {
                    mv.gotoPoint(2500, entry.getY());
                }
            } while (!rs.pincesAvantEmpty() && !rs.pincesArriereEmpty());

            if (step > 2) {
                complete();
            }

        } catch (NoPathFoundException | AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
        }
    }

    private double computeX(double baseX, boolean avant) {
        int coef = step * 120 + (avant ? 0 : 60); // FIXME nouvelle face avant

        if (rs.team() == ETeam.JAUNE) {
            return baseX - coef;
        } else {
            return baseX + coef;
        }
    }

}
