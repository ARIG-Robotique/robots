package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IEurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.MovementCancelledException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.strategy.actions.AbstractNerellAction;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NerellManchesAAir extends AbstractNerellAction {

    private final int xManche1 = 225;
    private final int xManche2 = 450;
    private final int xFinManche2 = 675;

    @Override
    public String name() {
        return IEurobotConfig.ACTION_MANCHE_A_AIR;
    }

    @Override
    public Point entryPoint() {
        double x = !rsNerell.mancheAAir1() ? xManche1 : xManche2;
        double y = 220;
        if (ETeam.JAUNE == rsNerell.team()) {
            x = 3000 - x;
        }

        return new Point(x, y);
    }

    @Override
    public int order() {
        int order = 0;
        if (!rsNerell.mancheAAir1() && !rsNerell.mancheAAir2()) {
            order += 15;
        } else {
            order += 10;
        }

        if (rsNerell.ecueilEquipePris()) {
            order += 30;
        }

        return order + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public boolean isValid() {
        return isTimeValid() && !rsNerell.inPort() && (!rsNerell.mancheAAir1() || !rsNerell.mancheAAir2());
    }

    @Override
    public void execute() {
        try {
            rsNerell.enablePincesAvant();
            final Point entry = entryPoint();
            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
            mv.pathTo(entry);

            final double y = entry.getY();
            boolean manche1Before = !rsNerell.mancheAAir1();
            if (manche1Before) {
                if (rsNerell.team() == ETeam.BLEU) {
                    mv.gotoOrientationDegSansDistance(150);
                    servosNerell.brasGaucheMancheAAir(true);
                    mv.gotoPoint(xManche2, y, GotoOption.ARRIERE);
                } else {
                    mv.gotoOrientationDegSansDistance(30);
                    servosNerell.brasDroitMancheAAir(true);
                    mv.gotoPoint(3000 - xManche2, y, GotoOption.ARRIERE);
                }
                group.mancheAAir1();
            }

            if (!rsNerell.mancheAAir2()) {
                if (rsNerell.team() == ETeam.BLEU) {
                    if (!manche1Before) {
                        mv.gotoOrientationDegSansDistance(180);
                        servosNerell.brasGaucheMancheAAir(true);
                    }
                    group.mancheAAir2(); // on stocke l'info avant le déplacement, cas de blocage sur fin du mouvement
                    mv.gotoPoint(xFinManche2, y, GotoOption.SANS_ORIENTATION, GotoOption.ARRIERE);
                } else {
                    if (!manche1Before) {
                        mv.gotoOrientationDegSansDistance(0);
                        servosNerell.brasDroitMancheAAir(true);
                    }
                    group.mancheAAir2();
                    mv.gotoPoint(3000 - xFinManche2, y, GotoOption.SANS_ORIENTATION, GotoOption.ARRIERE);
                }
            }

        } catch (MovementCancelledException e) {
            log.warn("Blocage mécanique sur la manche à air");

            if (!rs.mancheAAir1()) {
                group.mancheAAir1();
            } else if (!rs.mancheAAir2()) {
                group.mancheAAir2();
            }

        } catch (NoPathFoundException | AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'exécution de l'action : {}", e.toString());

        } finally {
            if (rsNerell.mancheAAir1() && rsNerell.mancheAAir2()) {
                complete();
            }

            servosNerell.brasDroitFerme(false);
            servosNerell.brasGaucheFerme(false);
        }
    }
}
