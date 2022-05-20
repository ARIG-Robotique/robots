package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.MovementCancelledException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Point;
import org.arig.robot.model.Team;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.model.enums.TypeCalage;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

import static org.arig.robot.constants.EurobotConfig.PTS_DEPOSE_PRISE;

@Slf4j
@Component
public class DistributeurEquipe extends AbstractDistributeur {

    private static final int DISTRIB_H = 102;

    private static final int ENTRY_X = 300;
    private static final int ENTRY_Y = 750;

    @Override
    public String name() {
        return EurobotConfig.ACTION_PRISE_DISTRIB_EQUIPE;
    }

    @Override
    public int order() {
        if (io.presenceStatuette(true)) {
            return 1000;
        }

        int points = 3 + 3 * PTS_DEPOSE_PRISE;
        return points + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public void refreshCompleted() {
        if (rs.distributeurEquipePris() || rs.distributeurEquipeBloque()) {
            complete();
        }
    }

    @Override
    public boolean isValid() {
        return isTimeValid() && remainingTimeBeforeRetourSiteValid()
                && !rs.distributeurEquipePris() && !rs.distributeurEquipeBloque() && rs.stockDisponible() >= 3
                && rs.getRemainingTime() >= EurobotConfig.invalidPriseEchantillonRemainingTime;
    }

    @Override
    public Point entryPoint() {
        return new Point(getX(ENTRY_X), ENTRY_Y);
    }

    @Override
    public void execute() {
        try {
            Point entry = entryPoint();
            mv.setVitesse(config.vitesse(), config.vitesseOrientation());
            mv.pathTo(entry);

            rs.disableAvoidance();

            // Calage sur X
            mv.setVitesse(config.vitesse(50), config.vitesseOrientation());
            mv.gotoOrientationDeg(rs.team() == Team.JAUNE ? 180 : 0);
            rs.enableCalageBordure(TypeCalage.AVANT_BAS);
            mv.avanceMM(ENTRY_X - DISTRIB_H - config.distanceCalageAvant() - 10);
            mv.setVitesse(config.vitesse(0), config.vitesseOrientation());
            rs.enableCalageBordure(TypeCalage.AVANT_BAS);
            mv.avanceMM(100);

            //if (!commonIOService.calageAvantBasDroit() || !commonIOService.calageAvantBasGauche()) {
            if (!rs.calageCompleted().contains(TypeCalage.AVANT_BAS)) {
                log.warn("Mauvaise position Y pour {}", name());
                updateValidTime(); // FIXME on devrait requérir un callage avant de recommencer
                rs.enableAvoidance();
                mv.setVitesse(config.vitesse(), config.vitesseOrientation());
                mv.gotoPoint(entry, GotoOption.ARRIERE);
                return;
            }

            CompletableFuture<?> task = prepare();

            mv.setVitesse(config.vitesse(50), config.vitesseOrientation());
            mv.reculeMM(ENTRY_X - DISTRIB_H - config.distanceCalageAvant() - 10);
            mv.gotoOrientationDeg(rs.team() == Team.JAUNE ? 180 : 0);

            task.join();
            prise();

            rs.enableAvoidance();
            mv.setVitesse(config.vitesse(), config.vitesseOrientation());
            mv.gotoPoint(entry, GotoOption.ARRIERE);

            group.distributeurEquipePris();
            complete(true);

        } catch (NoPathFoundException | AvoidingException e) {
            if (e instanceof MovementCancelledException) {
                final double robotX = mv.currentXMm();
                final double robotY = mv.currentYMm();

                // blocage dans la zone d'approche = un échantillon bloque le passage
                if ((robotX <= 350 || robotX >= 3000 - 350) && robotY <= 830 && robotY >= 670) {
                    log.warn("Blocage détecté à proximité de {}", name());
                    // FIXME Tenter de récupérer l'échantillon et le déposer derrière nous
                    group.distributeurEquipeBloque(); // on désactive l'action
                    return;
                }
            }

            log.error("Erreur d'exécution de l'action : {}", e.toString());
            updateValidTime();
            bras.safeHoming();
        }
    }
}
