package org.arig.robot.strategy.actions.active.tmp;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.model.CouleurEchantillon;
import org.arig.robot.model.Point;
import org.arig.robot.model.RobotName.RobotIdentification;
import org.arig.robot.model.Team;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.strategy.actions.AbstractEurobotAction;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PoussetteSiteEchantillonsEquipe extends AbstractEurobotAction {

    @Override
    public String name() {
        return EurobotConfig.TMP_ACTION_POUSSETTE_SITE_ECHANTILLONS_EQUIPE;
    }

    @Override
    public Point entryPoint() {
        return new Point(getX(910), 1720);
    }

    @Override
    public int order() {
        return 2000;
    }

    @Override
    public boolean isValid() {
        if (rs.twoRobots() && robotName.id() == RobotIdentification.ODIN) {
            return false;
        }
        return isTimeValid() && remainingTimeValid() && !rs.siteEchantillonPris();
    }

    @Override
    public void refreshCompleted() {
        if (rs.siteEchantillonPris() || (rs.twoRobots() && robotName.id() == RobotIdentification.ODIN)) {
            complete();
        }
    }

    @Override
    public void execute() {
        try {
            rs.enableAvoidance();
            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
            mv.gotoPoint(entryPoint());
            mv.gotoPoint(getX(1380), 1320);
            mv.gotoOrientationDeg(rs.team() == Team.JAUNE ? 0 : 180);
            commonServosService.groupeArriereOuvert(true);

            mv.setVitesse(robotConfig.vitesse(30), robotConfig.vitesseOrientation());
            mv.gotoPoint(getX(480), 1320, GotoOption.ARRIERE);
            group.siteEchantillonPris();

            rs.deposeCampementBleu(CouleurEchantillon.ROCHER, CouleurEchantillon.ROCHER, CouleurEchantillon.ROCHER);
            mv.avanceMM(100);
        } catch (AvoidingException e) {
            log.error("Erreur d'exécution de l'action : {}", e.toString());

        } finally {
            commonServosService.groupeArriereFerme(false);
            complete();
        }
    }
}
