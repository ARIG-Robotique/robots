package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Echantillon;
import org.arig.robot.model.Point;
import org.arig.robot.model.Team;
import org.arig.robot.model.bras.PositionBras;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.model.enums.TypeCalage;
import org.arig.robot.services.BrasService;
import org.arig.robot.strategy.actions.AbstractEurobotAction;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static org.arig.robot.constants.EurobotConfig.ECHANTILLON_SIZE;

@Slf4j
public abstract class AbstractPriseSiteDeFouille extends AbstractPriseEchantillon {

    protected abstract Echantillon.ID siteDeFouille();

    @Override
    public void execute() {
        try {
            final List<Echantillon> currentEchantillons = echantillonsSite(siteDeFouille());
            mv.setVitesse(config.vitesse(), config.vitesseOrientation());
            mv.pathTo(entryPoint(), GotoOption.AVANT);

            // Tant qu'il reste du temps et des échantillons dispo et du stock
            Iterator<Echantillon> echIt = currentEchantillons.iterator();
            do {
                // Preparation des bras pendant l'approche vers l'échantillon
                CompletableFuture<Void> task = runAsync(() -> {
                    bras.setBrasHaut(PositionBras.HORIZONTAL);
                    bras.setBrasBas(PositionBras.SOL_LEVEE);
                });

                // Approche vers l'échantillon
                final Echantillon echantillonAPrendre = echIt.next();
                final Point approcheEchantillon = tableUtils.eloigner(echantillonAPrendre, -config.distanceCalageAvant() - (ECHANTILLON_SIZE / 2.0));

                log.info("Approche de l'échantillon {}", echantillonAPrendre);
                mv.gotoPoint(approcheEchantillon, GotoOption.AVANT);
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

            } while (timeBeforeRetourValid() && echIt.hasNext() && rs.stockDisponible() > 0);
            bras.repos();

        } catch (NoPathFoundException | AvoidingException e) {
            log.error("Erreur d'exécution de l'action : {}", e.toString());
            updateValidTime();

        } finally {
            group.siteDeFouillePris();
            refreshCompleted();
            bras.safeHoming();
        }
    }
}
