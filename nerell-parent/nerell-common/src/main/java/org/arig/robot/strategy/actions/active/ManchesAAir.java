package org.arig.robot.strategy.actions.active;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.Point;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.enums.SensDeplacement;
import org.arig.robot.model.enums.SensRotation;
import org.arig.robot.services.ServosService;
import org.arig.robot.strategy.AbstractAction;
import org.arig.robot.system.ITrajectoryManager;
import org.arig.robot.utils.TableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ManchesAAir extends AbstractAction {

    @Autowired
    private ITrajectoryManager mv;

    @Autowired
    private RobotStatus rs;

    @Autowired
    private ServosService servos;

    @Autowired
    private TableUtils tableUtils;

    @Getter
    private boolean completed = false;

    private final int xManche1 = 225;
    private final int xManche2 = 450;
    private final int xFinManche2 = 675;

    @Override
    public String name() {
        return "Manches à Air";
    }

    @Override
    protected Point entryPoint() {
        double x = !rs.mancheAAir1() ? xManche1 : xManche2;
        double y = 220;
        if (ETeam.JAUNE == rs.getTeam()) {
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

        return order + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public boolean isValid() {
        return isTimeValid() && (!rs.mancheAAir1() || !rs.mancheAAir2());
    }

    @Override
    public void execute() {
        try {
            rs.enableAvoidance();
            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);

            final Point entry = entryPoint();
            mv.pathTo(entry);

            final double y = entry.getY();
            boolean manche1Before = !rs.mancheAAir1();
            if (manche1Before) {
                if (rs.getTeam() == ETeam.BLEU) {
                    mv.gotoOrientationDeg(150);
                    servos.brasGaucheMancheAAir(true);
                    mv.gotoPointMM(xManche2, y, true, SensDeplacement.ARRIERE);
                } else {
                    mv.gotoOrientationDeg(30);
                    servos.brasDroitMancheAAir(true);
                    mv.gotoPointMM(3000 - xManche2, y, true, SensDeplacement.ARRIERE);
                }
                rs.mancheAAir1(true);
            }

            if (!rs.mancheAAir2()) {
                if (rs.getTeam() == ETeam.BLEU) {
                    if (!manche1Before) {
                        mv.gotoOrientationDeg(180);
                        servos.brasGaucheMancheAAir(true);
                    }
                    mv.gotoPointMM(xFinManche2, y, false, SensDeplacement.ARRIERE);
                    mv.gotoOrientationDeg(-160, SensRotation.TRIGO);
                } else {
                    if (!manche1Before) {
                        mv.gotoOrientationDeg(0);
                        servos.brasDroitMancheAAir(true);
                    }
                    mv.gotoPointMM(3000 - xFinManche2, y, false, SensDeplacement.ARRIERE);
                    mv.gotoOrientationDeg(-20, SensRotation.HORAIRE);
                }
                rs.mancheAAir2(true);
            }
        } catch (NoPathFoundException | AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
        } finally {
            completed = rs.mancheAAir1() && rs.mancheAAir2();
            servos.brasDroitFerme(false);
            servos.brasGaucheFerme(false);
        }
    }
}
