package org.arig.robot.strategy.actions.active;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IEurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.services.INerellPincesArriereService;
import org.arig.robot.services.INerellPincesAvantService;
import org.arig.robot.strategy.actions.AbstractNerellAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.Rectangle;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Slf4j
@Component
public class NerellDeposeGrandPort extends AbstractNerellAction {

    @Autowired
    private INerellPincesAvantService pincesAvantService;

    @Autowired
    private INerellPincesArriereService pincesArriereService;

    private int step = 1;

    @Override
    public String name() {
        return IEurobotConfig.ACTION_DEPOSE_GRAND_PORT;
    }

    @Getter
    @Accessors(fluent = true)
    public List<String> blockingActions = Arrays.asList(
            IEurobotConfig.ACTION_DEPOSE_GRAND_PORT_ROUGE,
            IEurobotConfig.ACTION_DEPOSE_GRAND_PORT_VERT,
            IEurobotConfig.ACTION_NETTOYAGE_GRAND_PORT
    );

    @Override
    public Rectangle blockingZone() {
        return rsNerell.team() == ETeam.BLEU ? IEurobotConfig.ZONE_GRAND_PORT_BLEU : IEurobotConfig.ZONE_GRAND_PORT_JAUNE;
    }

    @Override
    public Point entryPoint() {
        return new Point(getX(230), 1200);
    }

    @Override
    public boolean isValid() {
        return isTimeValid() && !rsNerell.inPort() && (!rsNerell.pincesAvantEmpty() || !rsNerell.pincesArriereEmpty())
                && !rsNerell.grandChenalRougeEmpty() && !rsNerell.grandChenalVertEmpty() &&
                rsNerell.getRemainingTime() > IEurobotConfig.validRetourPortRemainingTimeNerell &&
                rsNerell.getRemainingTime() < IEurobotConfig.validRetourPortRemainingTimeNerell * 2;
    }

    @Override
    public int order() {
        long nbAvant = Stream.of(rsNerell.pincesAvant()).filter(Objects::nonNull).count();
        long nbArriere = Stream.of(rsNerell.pincesArriere()).filter(Objects::nonNull).count();
        return (int) Math.max(nbArriere, nbAvant) + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public void execute() {
        try {
            final Point entry = entryPoint();
            final Point entry2 = new Point(computeX(entry.getX(), !rsNerell.pincesAvantEmpty()), entry.getY());

            rsNerell.enablePincesAvant();
            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());

            long nbAvant = Stream.of(rsNerell.pincesAvant()).filter(Objects::nonNull).count();
            long nbArriere = Stream.of(rsNerell.pincesArriere()).filter(Objects::nonNull).count();
            GotoOption sens = nbArriere >= nbAvant ? GotoOption.ARRIERE : GotoOption.AVANT;

            if (tableUtils.distance(entry2) > 200) {
                mv.pathTo(entry2, sens);
                rsNerell.disableAvoidance();
            } else {
                rsNerell.disableAvoidance();
                mv.gotoPoint(entry2, GotoOption.SANS_ORIENTATION, sens);
            }

            mv.setVitesse(robotConfig.vitesse(50), robotConfig.vitesseOrientation());

            if (sens == GotoOption.AVANT) {
                if (rsNerell.team() == ETeam.BLEU) {
                    mv.gotoOrientationDeg(180);
                } else {
                    mv.gotoOrientationDeg(0);
                }
                pincesAvantService.deposeGrandPort();
                step++;
            } else {
                final Point entry3 = new Point(computeX(entry.getX(), false), entry.getY());
                mv.gotoPoint(entry3, GotoOption.SANS_ORIENTATION);
                if (rsNerell.team() == ETeam.BLEU) {
                    mv.gotoOrientationDeg(0);
                } else {
                    mv.gotoOrientationDeg(180);
                }
                pincesArriereService.deposeGrandPort();
                step++;
            }

            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
            mv.gotoPoint(getX(500), entry.getY());

            if (step > 3) {
                complete();
            }

        } catch (NoPathFoundException | AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'exécution de l'action : {}", e.toString());
        }
    }

    private double computeX(double baseX, boolean avant) {
        int coef = step * 90 + (avant ? 0 : 60); // FIXME nouvelle face avant

        if (rsNerell.team() == ETeam.JAUNE) {
            return baseX - coef;
        } else {
            return baseX + coef;
        }
    }

}
