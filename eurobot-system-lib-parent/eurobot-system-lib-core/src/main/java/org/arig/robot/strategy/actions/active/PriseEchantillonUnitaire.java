package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.*;
import org.arig.robot.model.bras.PositionBras;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.model.enums.TypeCalage;
import org.arig.robot.services.BrasService;
import org.arig.robot.strategy.actions.AbstractEurobotAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.concurrent.CompletableFuture;

import static org.arig.robot.constants.EurobotConfig.ECHANTILLON_SIZE;

@Slf4j
@Component
public class PriseEchantillonUnitaire extends AbstractPriseEchantillon {

    @Override
    public String name() {
        return EurobotConfig.ACTION_PRISE_ECHANTILLON_UNITAIRE;
    }

    @Override
    public int executionTimeMs() {
        return 2000;
    }

    @Override
    public boolean isValid() {
        return isTimeValid() && timeBeforeRetourValid() && rs.stockDisponible() > 0;
    }

    @Override
    public int order() {
        return EurobotConfig.PTS_DEPOSE_PRISE + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public Point entryPoint() {
        final Echantillon echantillon = echantillonPerdu();

        // Calcul point d'approche du site de fouille
        if (echantillon != null) {
            return new Point(echantillon.getX(), echantillon.getY());
        }

        // Pas d'entry point
        return new Point(0,0);
    }

    @Override
    public void execute() {
        final Echantillon echantillonAPrendre = echantillonPerdu();
        if (echantillonAPrendre == null) {
            log.warn("Pas d'échantillon à prendre de manière unitaire");
            updateValidTime();
            return;
        }

        CompletableFuture<Void> task;
        try {
            final Point approcheEchantillon = tableUtils.eloigner(entryPoint(), -config.distanceCalageAvant() - ECHANTILLON_SIZE / 2.0);
            log.info("Approche de l'échantillon {}", echantillonAPrendre);
            mv.setVitesse(config.vitesse(), config.vitesseOrientation());
            mv.pathTo(approcheEchantillon, GotoOption.AVANT);

            // Preparation des bras pendant l'approche vers l'échantillon
            task = runAsync(() -> {
                bras.setBrasHaut(PositionBras.HORIZONTAL);
                bras.setBrasBas(PositionBras.SOL_LEVEE);
            });

            // Alignement sur l'échantillon
            mv.alignFrontTo(echantillonAPrendre);

            // Avance vers l'échantillon
            double distanceEchantillon = ECHANTILLON_SIZE;
            log.info("Avance vers l'échantillon de {} mm", distanceEchantillon);
            mv.setVitesse(config.vitesse(0), config.vitesseOrientation());
            rs.enableCalageBordure(TypeCalage.PRISE_ECHANTILLON);
            mv.avanceMM(distanceEchantillon);

            if (rs.calageCompleted().contains(TypeCalage.PRISE_ECHANTILLON)) {
                task.join();
                bras.setBrasBas(PositionBras.SOL_PRISE);

                if (bras.waitEnableVentouseBas(echantillonAPrendre.getCouleur())) {
                    bras.setBrasBas(PositionBras.SOL_LEVEE); // on lève

                    if (EurobotConfig.ECHANGE_PRISE && echantillonAPrendre.getCouleur().isNeedsEchange()) {
                        if (bras.echangeBasHaut()) {
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
                } else {
                    bras.setBrasBas(PositionBras.SOL_LEVEE);
                }

            } else {
                log.warn("Calage de l'échantillon {} non terminé", echantillonAPrendre);
                task.join();
            }
            bras.repos();

        } catch (NoPathFoundException | AvoidingException e) {
            log.error("Erreur d'exécution de l'action : {}", e.toString());
            updateValidTime();

        } finally {
            bras.safeHoming();
        }
    }

}
