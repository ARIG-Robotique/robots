package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Point;
import org.arig.robot.model.Team;
import org.arig.robot.model.enums.TypeCalage;
import org.arig.robot.strategy.actions.AbstractEurobotAction;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DeposeStatuetteActivationVitrine extends AbstractEurobotAction {

    private static final int ENTRY_X = 240;
    private static final int ENTRY_Y = 1810;

    @Override
    public String name() {
        return EurobotConfig.ACTION_DEPOSE_STATUETTE;
    }

    @Override
    public int order() {
        int points = 0;
        if (rs.statuettePriseDansCeRobot() && io.presenceStatuette(true)) {
            points += 1000; // 15 points pour la dépose de statuette
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
        boolean validStatuette = rs.statuettePriseDansCeRobot() && io.presenceStatuette(true);

        return isTimeValid() && remainingTimeBeforeRetourSiteValid() && validStatuette;
    }

    @Override
    public Point entryPoint() {
        return new Point(getX(ENTRY_X), ENTRY_Y);
    }

    private Point secondaryEntryPoint() {
        return new Point(getX(ENTRY_X), 1500);
    }

    @Override
    public void execute() {
        try {
            Point entry = entryPoint();
            mv.setVitesse(config.vitesse(), config.vitesseOrientation());

            try {
                mv.pathTo(entry);
            } catch (NoPathFoundException e) {
                if (rs.tailleCampementRouge() == 0 && rs.tailleCampementBleu() == 0) {
                    entry = secondaryEntryPoint();
                    mv.pathTo(entry);
                } else {
                    throw e;
                }
            }

            rs.disableAvoidance(); // Zone interdite pour l'adversaire

            if (rs.statuettePriseDansCeRobot() && io.presenceStatuette(true)) {
                // Calage sur X
                mv.gotoOrientationDeg(rs.team() == Team.JAUNE ? 0 : 180);
                rs.enableCalageBordure(TypeCalage.ARRIERE, TypeCalage.FORCE);
                mv.reculeMM(ENTRY_X - config.distanceCalageArriere() - 10);
                mv.setVitesse(config.vitesse(10), config.vitesseOrientation());
                rs.enableCalageBordure(TypeCalage.ARRIERE, TypeCalage.FORCE);
                mv.reculeMMSansAngle(100);
                checkRecalageXmm(rs.team() == Team.JAUNE ? config.distanceCalageArriere() : EurobotConfig.tableWidth - config.distanceCalageArriere());
                checkRecalageAngleDeg(rs.team() == Team.JAUNE ? 0 : 180);
                mv.setVitesse(config.vitesse(), config.vitesseOrientation());
                mv.avanceMM(ENTRY_X - config.distanceCalageArriere());
            }

            // Calage sur Y
            mv.gotoOrientationDeg(-90);
            rs.enableCalageBordure(TypeCalage.ARRIERE, TypeCalage.FORCE);
            mv.reculeMM(EurobotConfig.tableHeight - entry.getY() - config.distanceCalageArriere() - 10);
            mv.setVitesse(config.vitesse(10), config.vitesseOrientation());
            rs.enableCalageBordure(TypeCalage.ARRIERE, TypeCalage.FORCE);
            mv.reculeMMSansAngle(100);
            checkRecalageYmm(EurobotConfig.tableHeight - config.distanceCalageArriere());
            checkRecalageAngleDeg(-90);
            group.vitrineActive(); // Vitrine active sur front

            // Si on as la statuette dans le robot, on la dépose
            if (rs.statuettePriseDansCeRobot() && io.presenceStatuette(true)) {
                servos.fourcheStatuettePriseDepose(true);
                group.statuetteDansVitrine();
            }

            mv.setVitesse(config.vitesse(50), config.vitesseOrientation());
            mv.avanceMM(100);
            servos.fourcheStatuetteFerme(false);

        } catch (NoPathFoundException | AvoidingException e) {
            log.error("Erreur d'exécution de l'action : {}", e.toString());
            updateValidTime();
        } finally {
            servos.fourcheStatuetteFerme(false);
            refreshCompleted();
        }
    }
}
