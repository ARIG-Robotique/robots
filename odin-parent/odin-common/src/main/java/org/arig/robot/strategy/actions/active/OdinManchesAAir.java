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
import org.arig.robot.model.enums.SensRotation;
import org.arig.robot.strategy.actions.AbstractOdinAction;
import org.springframework.stereotype.Component;

import java.awt.Rectangle;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class OdinManchesAAir extends AbstractOdinAction {

    private final int xManche1 = 170;
    private final int xManche2 = 500;
    private final int xFinManche2 = 680;

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
        double y = 160;
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
    public void refreshCompleted() {
        if (rsOdin.mancheAAir1() && rsOdin.mancheAAir2()) {
            complete();
        }
    }

    @Override
    public void execute() {
        try {
            rsOdin.enablePincesAvant();
            rsOdin.enablePincesArriere();
            final Point entry = entryPoint();
            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
            mv.pathTo(entry);

            mv.gotoOrientationDegSansDistance(rs.team() == ETeam.BLEU ? 0 : 180 );

            final double y = entry.getY();
            boolean manche1Before = !rsOdin.mancheAAir1();
            if (manche1Before) {
                mv.setVitesse(robotConfig.vitesse(0), robotConfig.vitesseOrientation());
                rs.enableCalageBordure();
                mv.reculeMMSansAngle(500);
                mv.setVitesse(robotConfig.vitesse(80), robotConfig.vitesseOrientation());

                // On active avec le bras droit
                if (rs.team() == ETeam.BLEU) {
                    servosOdin.brasDroitMancheAAir(true);
                } else {
                    servosOdin.brasGaucheMancheAAir(true);
                }

                group.mancheAAir1();

                mv.gotoPoint(getX(xManche2), y, GotoOption.SANS_ORIENTATION);
            }

            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
            if (!rsOdin.mancheAAir2()) {
                group.mancheAAir2();

                // On active avec le bras droit
                if (rs.team() == ETeam.BLEU) {
                    servosOdin.brasDroitMancheAAir(true);
                } else {
                    servosOdin.brasGaucheMancheAAir(true);
                }

                mv.gotoPoint(getX(xFinManche2), y, GotoOption.SANS_ORIENTATION);
                mv.gotoOrientationDeg(90, SensRotation.AUTO);

                servosOdin.brasDroitFerme(false);
                servosOdin.brasGaucheFerme(false);
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
