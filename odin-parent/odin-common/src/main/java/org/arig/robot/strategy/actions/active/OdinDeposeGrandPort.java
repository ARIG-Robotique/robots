package org.arig.robot.strategy.actions.active;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IEurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.services.AbstractOdinPincesArriereService;
import org.arig.robot.services.AbstractOdinPincesAvantService;
import org.arig.robot.strategy.actions.AbstractOdinAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class OdinDeposeGrandPort extends AbstractOdinAction {

    @Autowired
    private AbstractOdinPincesAvantService pincesAvantService;

    @Autowired
    private AbstractOdinPincesArriereService pincesArriereService;

    private int step = 0;

    @Override
    public String name() {
        return IEurobotConfig.ACTION_DEPOSE_GRAND_PORT;
    }

    @Getter
    @Accessors(fluent = true)
    public List<String> blockingActions = Arrays.asList(
            IEurobotConfig.ACTION_DEPOSE_GRAND_PORT_ROUGE,
            IEurobotConfig.ACTION_DEPOSE_GRAND_PORT_VERT
    );

    @Override
    public Point entryPoint() {
        double X = 230;
        double Y = 1200;
        if (ETeam.JAUNE == rsOdin.team()) {
            X = 3000 - X;
        }
        return new Point(X, Y);
    }

    @Override
    public boolean isValid() {
        boolean valid = isTimeValid() && !rsOdin.inPort() && (!rsOdin.pincesAvantEmpty() || !rsOdin.pincesArriereEmpty())
                && rsOdin.getRemainingTime() > IEurobotConfig.validRetourPortRemainingTimeOdin;
        if (rs.twoRobots()) {
            valid = valid && !rs.grandChenalRougeEmpty() && !rs.grandChenalVertEmpty();
        }

        return valid;
    }

    @Override
    public int order() {
        int order = 0;
        for (ECouleurBouee eCouleurBouee : rsOdin.pincesAvant()) {
            if (eCouleurBouee != null) {
                order++;
            }
        }
        for (ECouleurBouee eCouleurBouee : rsOdin.pincesArriere()) {
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
            final Point entry2 = new Point(computeX(entry.getX()), entry.getY());

            rsOdin.enablePincesAvant();
            rsOdin.enablePincesArriere();
            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());

            GotoOption sens = !rsOdin.pincesAvantEmpty() ? GotoOption.AVANT : GotoOption.ARRIERE;
            if (tableUtils.distance(entry2) > 200) {
                mv.pathTo(entry2, sens);
                rsOdin.disableAvoidance();
            } else {
                rsOdin.disableAvoidance();
                mv.gotoPoint(entry2, GotoOption.SANS_ORIENTATION, sens);
            }

            mv.setVitesse(robotConfig.vitesse(50), robotConfig.vitesseOrientation());

            do {
                if (!rsOdin.pincesAvantEmpty()) {
                    if (rsOdin.team() == ETeam.BLEU) {
                        mv.gotoOrientationDeg(180);
                    } else {
                        mv.gotoOrientationDeg(0);
                    }
                    pincesAvantService.deposeGrandPort();
                    step++;
                }

                if (!rsOdin.pincesArriereEmpty()) {
                    final Point entry3 = new Point(computeX(entry.getX()), entry.getY());
                    mv.gotoPoint(entry3, GotoOption.SANS_ORIENTATION);
                    if (rsOdin.team() == ETeam.BLEU) {
                        mv.gotoOrientationDeg(0);
                    } else {
                        mv.gotoOrientationDeg(180);
                    }
                    pincesArriereService.deposeGrandPort();
                    step++;
                }

                mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
                if (rsOdin.team() == ETeam.BLEU) {
                    mv.gotoPoint(500, entry.getY());
                } else {
                    mv.gotoPoint(2500, entry.getY());
                }
            } while (!rsOdin.pincesAvantEmpty() && !rsOdin.pincesArriereEmpty());

            if (step > 2) {
                complete();
            }

        } catch (NoPathFoundException | AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'exécution de l'action : {}", e.toString());
        }
    }

    private double computeX(double baseX) {
        int coef = step * 120;

        if (rsOdin.team() == ETeam.JAUNE) {
            return baseX - coef;
        } else {
            return baseX + coef;
        }
    }
}
