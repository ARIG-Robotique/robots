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

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static org.arig.robot.constants.EurobotConfig.ECHANTILLON_SIZE;

@Slf4j
@Component
public class SiteFouilleEquipe extends AbstractEurobotAction {

    private static final int CENTRE_FOUILLE_X = 975;
    private static final int CENTRE_FOUILLE_Y = 625;

    @Autowired
    private BrasService bras;

    @Override
    public String name() {
        return EurobotConfig.ACTION_PRISE_SITE_FOUILLE_EQUIPE;
    }

    @Override
    public List<String> blockingActions() {
        return Collections.singletonList(EurobotConfig.ACTION_DECOUVERTE_CARRE_FOUILLE);
    }

    @Override
    public int executionTimeMs() {
        return 4000 * 3; // TODO: A quantifier
    }

    @Override
    public boolean isValid() {
        if (rs.strategy() != Strategy.FOUILLE) {
            return false;
        }

        return isTimeValid() && timeBeforeRetourValid()
                && !rs.siteDeFouillePris() && rs.stockDisponible() > 0;
    }

    @Override
    public void refreshCompleted() {
        if (rs.siteDeFouillePris() || rs.strategy() != Strategy.FOUILLE) {
            complete();
        }
    }

    @Override
    public int order() {
        if (rs.strategy() == Strategy.FOUILLE
                && rs.twoRobots() && (robotName.id() == RobotName.RobotIdentification.ODIN)) {
            // Si c'est Odin et que la strat est fouille avec deux robots
            // C'est la première action
            return 1000;
        }

        int stock = rs.stockDisponible();
        return Math.min(stock, 3) * EurobotConfig.PTS_DEPOSE_PRISE + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public Point entryPoint() {
        final List<Echantillon> currentEchantillons = echantillonsSiteDeFouille();

        // Calcul point d'approche du site de fouille
        if (!currentEchantillons.isEmpty()) {
            final Echantillon echantillonPlusProche = currentEchantillons.get(0);
            final Point centreSteDeFouille = new Point(getX(CENTRE_FOUILLE_X), CENTRE_FOUILLE_Y);
            return new Point(
                    centreSteDeFouille.getX() -
                            Math.signum(centreSteDeFouille.getX() - echantillonPlusProche.getX()) * EurobotConfig.PATHFINDER_SITE_FOUILLE_SIZE / 2,
                    centreSteDeFouille.getY() -
                            Math.signum(centreSteDeFouille.getY() - echantillonPlusProche.getY()) * EurobotConfig.PATHFINDER_SITE_FOUILLE_SIZE / 2);
        }

        // Pas d'entry point
        return new Point(0,0);
    }

    @Override
    public void execute() {
        CompletableFuture<Void> task;
        try {
            final List<Echantillon> currentEchantillons = echantillonsSiteDeFouille();
            mv.setVitesse(config.vitesse(), config.vitesseOrientation());
            mv.pathTo(entryPoint(), GotoOption.AVANT);

            // Tant qu'il reste du temps et des échantillons dispo et du stock
            Iterator<Echantillon> echIt = currentEchantillons.iterator();
            do {
                // Preparation des bras pendant l'approche vers l'échantillon
                task = runAsync(() -> {
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

    private List<Echantillon> echantillonsSiteDeFouille() {
        final Point positionCourante = new Point(mv.currentXMm(), mv.currentYMm());
        return rs.echantillons().getEchantillons().stream()
                .filter(e -> e.getId() != null)
                .filter(e -> e.getId().equals(rs.team() == Team.JAUNE ? Echantillon.ID.SITE_FOUILLE_JAUNE : Echantillon.ID.SITE_FOUILLE_VIOLET))
                .sorted(Comparator.comparing(e -> e.distance(positionCourante)))
                .map(Echantillon::clone)
                .collect(Collectors.toList());
    }
}
