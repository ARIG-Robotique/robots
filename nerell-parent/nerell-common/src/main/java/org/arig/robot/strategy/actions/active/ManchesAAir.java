package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.strategy.actions.AbstractNerellAction;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ManchesAAir extends AbstractNerellAction {

    private final int xManche1 = 225;
    private final int xManche2 = 450;
    private final int xFinManche2 = 675;

    @Override
    public String name() {
        return "Manches à Air";
    }

    @Override
    public Point entryPoint() {
        double x = !rs.mancheAAir1() ? xManche1 : xManche2;
        double y = 220;
        if (ETeam.JAUNE == rs.team()) {
            x = 3000 - x;
        }

        return new Point(x, y);
    }

    @Override
    public int order() {
        int order = 0;
        if (!rs.mancheAAir1() && !rs.mancheAAir2()) {
            order += 15;
        } else {
            order += 10;
        }

        if (rs.ecueilEquipePris()) {
            order += 30;
        }

        return order + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public boolean isValid() {
        return isTimeValid() && (!rs.mancheAAir1() || !rs.mancheAAir2());
    }

    @Override
    public void execute() {
        try {
            rs.enablePincesAvant();
            final Point entry = entryPoint();
            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
            mv.pathTo(entry);

            final double y = entry.getY();
            boolean manche1Before = !rs.mancheAAir1();
            if (manche1Before) {
                if (rs.team() == ETeam.BLEU) {
                    mv.gotoOrientationDegSansDistance(150);
                    servos.brasGaucheMancheAAir(true);
                    mv.gotoPoint(xManche2, y, GotoOption.ARRIERE);
                } else {
                    mv.gotoOrientationDegSansDistance(30);
                    servos.brasDroitMancheAAir(true);
                    mv.gotoPoint(3000 - xManche2, y, GotoOption.ARRIERE);
                }
                rs.mancheAAir1(true);
            }

            if (!rs.mancheAAir2()) {
                if (rs.team() == ETeam.BLEU) {
                    if (!manche1Before) {
                        mv.gotoOrientationDegSansDistance(180);
                        servos.brasGaucheMancheAAir(true);
                    }
                    rs.mancheAAir2(true); // on stocke l'info avant le déplacement, cas de blocage sur fin du mouvement
                    mv.gotoPoint(xFinManche2, y, GotoOption.SANS_ORIENTATION, GotoOption.ARRIERE);
                } else {
                    if (!manche1Before) {
                        mv.gotoOrientationDegSansDistance(0);
                        servos.brasDroitMancheAAir(true);
                    }
                    rs.mancheAAir2(true);
                    mv.gotoPoint(3000 - xFinManche2, y, GotoOption.SANS_ORIENTATION, GotoOption.ARRIERE);
                }
            }
        } catch (NoPathFoundException | AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
        } finally {
            if (rs.mancheAAir1() && rs.mancheAAir2()) {
                complete();
            }

            servos.brasDroitFerme(false);
            servos.brasGaucheFerme(false);
        }
    }
}
