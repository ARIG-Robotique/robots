package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Point;
import org.arig.robot.model.Team;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.model.enums.TypeCalage;
import org.arig.robot.strategy.actions.AbstractEurobotAction;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DeposeStatuetteActivationVitrine extends AbstractEurobotAction {

    @Override
    public String name() {
        return EurobotConfig.ACTION_DEPOSE_STATUETTE;
    }

    @Override
    public int order() {
        int points = 0;
        if (rs.statuettePrisDansCeRobot()) {
            points += 15; // 15 points pour la dépose de statuette
        }
        if (!rs.vitrineActive()) {
             points += 5; // 5 point de plus si vitrine inactive
        }
        return points + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public void refreshCompleted() {
        if (rs.statuetteDansVitrine() && rs.vitrineActive()) {
            complete();
        }
    }

    @Override
    public boolean isValid() {
        // Valid si on est chargé de la statuette
        boolean validStatuette = rs.statuettePrisDansCeRobot();

        // Valid si la vitrine n'est pas encore activé dans les 30 dernières secondes de match
        boolean validVitrine = !rs.vitrineActive() && rs.getRemainingTime() < EurobotConfig.validActivationVitrineRemainingTime;

        return (validStatuette || validVitrine) && isTimeValid() && remainingTimeValid();
    }

    @Override
    public Point entryPoint() {
        return new Point(getX(240), 1810);
    }

    @Override
    public void execute() {
        try {
            Point entry = entryPoint();
            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
            mv.pathTo(entry);

            rs.disableAvoidance(); // Zone interdite pour l'adversaire

            // Calage sur X
            mv.gotoOrientationDeg(rs.team() == Team.JAUNE ? 0 : 180);
            rs.enableCalageBordure(TypeCalage.ARRIERE, TypeCalage.FORCE);
            mv.reculeMM(240 - robotConfig.distanceCalageArriere() - 10);
            mv.setVitesse(robotConfig.vitesse(0), robotConfig.vitesseOrientation());
            rs.enableCalageBordure(TypeCalage.ARRIERE, TypeCalage.FORCE);
            mv.reculeMMSansAngle(30);
            checkRecalageXmm(rs.team() == Team.JAUNE ? robotConfig.distanceCalageArriere() : EurobotConfig.tableWidth - robotConfig.distanceCalageArriere());
            checkRecalageAngleDeg(rs.team() == Team.JAUNE ? 0 : 180);
            mv.avanceMM(240 - robotConfig.distanceCalageArriere());

            // Calage sur Y
            mv.gotoOrientationDeg(-90);
            rs.enableCalageBordure(TypeCalage.ARRIERE, TypeCalage.FORCE);
            mv.reculeMM(EurobotConfig.tableHeight - entry.getY() - robotConfig.distanceCalageArriere() - 10);
            mv.setVitesse(robotConfig.vitesse(0), robotConfig.vitesseOrientation());
            rs.enableCalageBordure(TypeCalage.ARRIERE, TypeCalage.FORCE);
            mv.reculeMMSansAngle(30);
            checkRecalageYmm(EurobotConfig.tableHeight - robotConfig.distanceCalageArriere());
            checkRecalageAngleDeg(-90);

            // Si on as la statuette dans le robot, on la dépose
            if (rs.statuettePrisDansCeRobot()) {
                commonServosService.fourcheStatuettePriseDepose(true);
                group.statuetteDansVitrine();
            }

            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
            mv.avanceMM(100);
            commonServosService.fourcheStatuetteFerme(false);
            group.vitrineActive();
            mv.gotoPoint(entry, GotoOption.SANS_ORIENTATION);

        } catch (NoPathFoundException | AvoidingException e) {
            log.error("Erreur d'exécution de l'action : {}", e.toString());
            updateValidTime();
        } finally {
            commonServosService.fourcheStatuetteFerme(false);
            refreshCompleted();
        }
    }
}
