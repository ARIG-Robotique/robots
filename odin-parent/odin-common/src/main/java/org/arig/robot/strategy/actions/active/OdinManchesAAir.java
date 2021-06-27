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
import org.arig.robot.strategy.actions.AbstractOdinAction;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class OdinManchesAAir extends AbstractOdinAction {

    private final int xManche1 = 170;
    private final int xManche2 = 500;
    private final int xFinManche2 = 760;
    private final int xEndAction = 720;

    @Override
    public String name() {
        return IEurobotConfig.ACTION_MANCHE_A_AIR;
    }

    @Getter
    @Accessors(fluent = true)
    public List<String> blockingActions = Arrays.asList(
            IEurobotConfig.ACTION_ECUEIL_EQUIPE
    );

    @Override
    public Rectangle blockingZone() {
        return rsOdin.team() == ETeam.BLEU ? IEurobotConfig.ZONE_ECUEIL_EQUIPE_BLEU : IEurobotConfig.ZONE_ECUEIL_EQUIPE_JAUNE;
    }

    @Override
    public Point entryPoint() {
        double x = !rsOdin.mancheAAir1() ? xManche1 : xManche2;
        double y = 170;
        if (ETeam.JAUNE == rsOdin.team()) {
            x = 3000 - x;
        }

        return new Point(x, y);
    }

    @Override
    public int order() {
        int order = 0;
        if (!rsOdin.mancheAAir1() && !rsOdin.mancheAAir2()) {
            order += 15;
        } else {
            order += 10;
        }

        return order + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public boolean isValid() {
        return isTimeValid() && !rsOdin.inPort() && (!rsOdin.mancheAAir1() || !rsOdin.mancheAAir2());
    }

    @Override
    public void execute() {
        try {
            rsOdin.enablePincesAvant();
            rsOdin.enablePincesArriere();
            final Point entry = entryPoint();
            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
            mv.pathTo(entry);

            final double angleRobot = conv.pulseToDeg(position.getAngle());
            if (Math.abs(angleRobot) <= 90) {
                mv.gotoOrientationDegSansDistance(0);
                // On active avec le bras droit
                servosOdin.brasDroitMancheAAir(true);
            } else {
                mv.gotoOrientationDegSansDistance(180);
                // On active avec le bras gauche
                servosOdin.brasGaucheMancheAAir(true);
            }

            final double y = entry.getY();
            boolean manche1Before = !rsOdin.mancheAAir1();
            if (manche1Before) {
                if (rsOdin.team() == ETeam.BLEU) {
                    mv.gotoPoint(xManche2, y, GotoOption.SANS_ORIENTATION);
                } else {
                    mv.gotoPoint(3000 - xManche2, y, GotoOption.SANS_ORIENTATION);
                }
                group.mancheAAir1();
            }

            if (!rsOdin.mancheAAir2()) {
                if (rsOdin.team() == ETeam.BLEU) {
                    mv.gotoPoint(xFinManche2, y, GotoOption.SANS_ORIENTATION);
                } else {
                    mv.gotoPoint(3000 - xFinManche2, y, GotoOption.SANS_ORIENTATION);
                }
                group.mancheAAir2();

                servosOdin.brasDroitFerme(false);
                servosOdin.brasGaucheFerme(false);

                if (rsOdin.team() == ETeam.BLEU) {
                    mv.gotoPoint(xEndAction, y, GotoOption.SANS_ORIENTATION);
                } else {
                    mv.gotoPoint(3000 - xEndAction, y, GotoOption.SANS_ORIENTATION);
                }
            }
        } catch (NoPathFoundException | AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'exécution de l'action : {}", e.toString());
        } finally {
            if (rsOdin.mancheAAir1() && rsOdin.mancheAAir2()) {
                complete();
            }

            servosOdin.brasDroitFerme(false);
            servosOdin.brasGaucheFerme(false);
        }
    }
}
