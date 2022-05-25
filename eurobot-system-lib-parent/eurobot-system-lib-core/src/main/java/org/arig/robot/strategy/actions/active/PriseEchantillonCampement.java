package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.CouleurEchantillon;
import org.arig.robot.model.Point;
import org.arig.robot.model.Team;
import org.arig.robot.model.bras.PositionBras;
import org.arig.robot.model.enums.TypeCalage;
import org.arig.robot.services.BrasService;
import org.arig.robot.strategy.actions.AbstractEurobotAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.arig.robot.strategy.actions.active.DeposeStatuetteActivationVitrine.ENTRY_X_DEPOSE_STATUETTE;

@Slf4j
@Component
public class PriseEchantillonCampement extends AbstractEurobotAction {

    @Autowired
    private BrasService bras;

    public String name() {
        return EurobotConfig.ACTION_PRISE_ECHANTILLON_DISTRIBUTEUR_CAMPEMENT;
    }

    @Override
    public int executionTimeMs() {
        return 0;
    }

    @Override
    public int order() {
        return EurobotConfig.PTS_DEPOSE_PRISE + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public boolean isValid() {
        return isTimeValid() && timeBeforeRetourValid() && !rs.echantillonCampementPris() && rs.stockDisponible() > 0;
    }

    @Override
    public List<String> blockingActions() {
        return Collections.singletonList(EurobotConfig.ACTION_DEPOSE_STATUETTE);
    }

    @Override
    public void refreshCompleted() {
        if (rs.echantillonCampementPris()) {
            complete();
        }
    }

    @Override
    public Point entryPoint() {
        // y = 300 position theorique de l'echantillon
        return new Point(getX(ENTRY_X_DEPOSE_STATUETTE), 1700);
    }

    @Override
    public void execute() {
        CompletableFuture<Void> task = execute(false);
        if (task != null) {
            task.join();
        }
    }

    CompletableFuture<Void> execute(boolean skipPath) {
        CompletableFuture<Void> task = null;
        try {
            if (!skipPath) {
                mv.setVitesse(config.vitesse(), config.vitesseOrientation());
                mv.pathTo(entryPoint());
            }

            rs.disableAvoidance();

            task = runAsync(() -> {
                // Preparation des position des bras pour la prise
                bras.setBrasHaut(PositionBras.HORIZONTAL);
                bras.setBrasBas(PositionBras.BORDURE_APPROCHE);
            });

            // rotation 0 (jaune) ou 180 (violet)
            mv.gotoOrientationDeg(rs.team() == Team.JAUNE ? 180 : 0);

            // tout droit calage bodure + force
            rs.enableCalageBordure(TypeCalage.AVANT_BAS, TypeCalage.AVANT_HAUT, TypeCalage.FORCE);
            mv.setVitesse(config.vitesse(), config.vitesseOrientation());
            mv.avanceMM(ENTRY_X_DEPOSE_STATUETTE - config.distanceCalageAvant() - 10);

            task.join();

            rs.enableCalageBordure(TypeCalage.AVANT_BAS, TypeCalage.AVANT_HAUT, TypeCalage.FORCE);
            mv.setVitesse(config.vitesse(10), config.vitesseOrientation());
            mv.avanceMMSansAngle(20);
            checkRecalageXmm(rs.team() == Team.JAUNE ? config.distanceCalageAvant() : EurobotConfig.tableWidth - config.distanceCalageAvant(), TypeCalage.AVANT_BAS, TypeCalage.AVANT_HAUT);
            checkRecalageAngleDeg(rs.team() == Team.JAUNE ? 180 : 0, TypeCalage.AVANT_BAS, TypeCalage.AVANT_HAUT);

            // prise echantillon
            bras.setBrasBas(PositionBras.BORDURE_PRISE);
            boolean priseOK = bras.waitEnableVentouseBas(CouleurEchantillon.ROCHER_VERT);
            bras.setBrasBas(PositionBras.BORDURE_APPROCHE);
            mv.reculeMM(ENTRY_X_DEPOSE_STATUETTE - config.distanceCalageAvant());

            if (priseOK) {
                group.echantillonCampementPris();
                task = runAsync(() -> {
                    if (EurobotConfig.ECHANGE_PRISE) {
                        if (bras.echangeBasHaut(true)) {
                            bras.setBrasBas(PositionBras.HORIZONTAL);
                            bras.stockageHaut();
                            bras.setBrasHaut(PositionBras.HORIZONTAL);
                        } else {
                            bras.setBrasHaut(PositionBras.HORIZONTAL);
                            bras.setBrasBas(PositionBras.STOCK_ENTREE);
                        }
                    } else {
                        bras.stockageBas();
                    }
                    bras.repos();
                });
            } else {
                bras.repos();
            }
            complete();

        } catch (NoPathFoundException | AvoidingException e) {
            log.error("Erreur d'exécution de l'action : {}", e.toString());
            updateValidTime();
            bras.safeHoming();
        } finally {
            refreshCompleted();
        }

        return task;
    }
}
