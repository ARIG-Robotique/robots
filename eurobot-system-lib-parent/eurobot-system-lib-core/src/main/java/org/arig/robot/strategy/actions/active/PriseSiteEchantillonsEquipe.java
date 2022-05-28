package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.CouleurEchantillon;
import org.arig.robot.model.Point;
import org.arig.robot.model.RobotName;
import org.arig.robot.model.Strategy;
import org.arig.robot.model.bras.PositionBras;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.model.enums.TypeCalage;
import org.arig.robot.services.BrasService;
import org.arig.robot.strategy.actions.AbstractEurobotAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

import static org.arig.robot.constants.EurobotConfig.ECHANTILLON_SIZE;
import static org.arig.robot.constants.EurobotConfig.PTS_DEPOSE_PRISE;

@Slf4j
@Component
public class PriseSiteEchantillonsEquipe extends AbstractEurobotAction {

    @Autowired
    private BrasService bras;

    private boolean firstAction = false;
    private CouleurEchantillon echantillonEntry;

    @Override
    public String name() {
        return EurobotConfig.ACTION_PRISE_ECHANTILLONS_SITE_EQUIPE;
    }

    @Override
    public int executionTimeMs() {
        return 4000 * 3;
    }

    @Override
    public int order() {
        if (rs.strategy() == Strategy.BASIC && (
                (robotName.id() == RobotName.RobotIdentification.NERELL) || (!rs.twoRobots() && robotName.id() == RobotName.RobotIdentification.ODIN)
        )) {
            // Si c'est Nerell et que la strat est la basique ou aggressive.
            // Ou si c'est Odin et qu'il n'y a qu'un seul robot en strat basique.
            // C'est la première action
            firstAction = true;
            return 1000;
        }

        int points = 3 * PTS_DEPOSE_PRISE;
        return points + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public boolean isValid() {
        if (rs.strategy() == Strategy.FINALE_1 || rs.strategy() == Strategy.FINALE_2) {
            rs.siteEchantillonPris(true);
            return false;
        }
        return isTimeValid() && timeBeforeRetourValid() && !rs.siteEchantillonPris();
    }

    @Override
    public void refreshCompleted() {
        if (rs.siteEchantillonPris()) {
            complete();
        }
    }

    @Override
    public Point entryPoint() {
        if (firstAction) {
            echantillonEntry = CouleurEchantillon.ROCHER_VERT;
            return pointEchantillonVert();
        }

        // Si ce n'est pas la première exec, on aborde par le haut ou par le bas
        final double distanceRouge = tableUtils.distance(pointEchantillonRouge());
        final double distanceBleu = tableUtils.distance(pointEchantillonBleu());

        final double distanceMin = Math.min(distanceRouge, distanceBleu);
        if (distanceMin == distanceRouge) {
            echantillonEntry = CouleurEchantillon.ROCHER_ROUGE;
            return pointEchantillonRouge();
        } else {
            echantillonEntry = CouleurEchantillon.ROCHER_BLEU;
            return pointEchantillonBleu();
        }
    }

    private Point pointEchantillonVert() {
        return new Point(getX(830), 1325);
    }

    private Point pointEchantillonRouge() {
        return new Point(getX(900), 1205);
    }

    private Point pointEchantillonBleu() {
        return new Point(getX(900), 1445);
    }

    @Override
    public void execute() {
        try {
            entryPoint(); // On récupère le point d'entrée et on rafraichit l'échantillon

            CompletableFuture<Void> task = CompletableFuture.completedFuture(null);
            if (firstAction) {
                mv.gotoPoint(getX(800), 1700, GotoOption.SANS_ORIENTATION);
                mv.gotoPoint(getX(1060), 1700);
                task = priseEchantillon(task, false, false, false, pointEchantillonBleu(), CouleurEchantillon.ROCHER_BLEU);
                task = priseEchantillon(task, false, false, false, pointEchantillonVert(), CouleurEchantillon.ROCHER_VERT);
                task = priseEchantillon(task, false, false, true, pointEchantillonRouge(), CouleurEchantillon.ROCHER_ROUGE);
            } else {
                if (echantillonEntry == CouleurEchantillon.ROCHER_ROUGE) {
                    // De bas en haut
                    task = priseEchantillon(task, true, true, false, pointEchantillonRouge(), CouleurEchantillon.ROCHER_ROUGE);
                    task = priseEchantillon(task, false, false, false, pointEchantillonVert(), CouleurEchantillon.ROCHER_VERT);
                    task = priseEchantillon(task, false, false, false, pointEchantillonBleu(), CouleurEchantillon.ROCHER_BLEU);
                } else {
                    // De haut en bas
                    task = priseEchantillon(task, true, true, false, pointEchantillonBleu(), CouleurEchantillon.ROCHER_BLEU);
                    task = priseEchantillon(task, false, false, false, pointEchantillonVert(), CouleurEchantillon.ROCHER_VERT);
                    task = priseEchantillon(task, false, false, false, pointEchantillonRouge(), CouleurEchantillon.ROCHER_ROUGE);
                }
            }

            task.join();
            runAsync(() -> bras.repos());

            group.siteEchantillonPris();

        } catch (NoPathFoundException | AvoidingException e) {
            log.error("Erreur d'exécution de l'action : {}", e.toString());
            updateValidTime();
            bras.safeHoming();

        } finally {
            refreshCompleted();
            firstAction = false;
        }
    }

    private CompletableFuture<Void> priseEchantillon(
            CompletableFuture<Void> previousTask,
            boolean path,
            boolean first,
            boolean pousseRouge,
            Point pointEchantillon,
            CouleurEchantillon couleur
    ) throws AvoidingException, NoPathFoundException {

        mv.setVitesse(config.vitesse(), config.vitesseOrientation());
        final Point dest = tableUtils.eloigner(pointEchantillon, -config.distanceCalageAvant() - (ECHANTILLON_SIZE / 3.0));

        final CompletableFuture<Void> task;

        if (path) { // implique first
            mv.pathTo(dest, GotoOption.AVANT);

            // après le path, pendant le calage, on met les bras en position initiale
            task = previousTask.thenRunAsync(() -> {
                bras.setBrasHaut(PositionBras.HORIZONTAL);
                bras.setBrasBas(PositionBras.SOL_LEVEE);
            }, executor);

        } else {
            // pendant le mouvement et le calage, on met les bras en position initiale
            task = previousTask.thenRunAsync(() -> {
                bras.setBrasHaut(PositionBras.HORIZONTAL);
                bras.setBrasBas(PositionBras.SOL_LEVEE);
            }, executor);

            if (first) {
                mv.gotoPoint(dest, GotoOption.AVANT, GotoOption.SANS_ORIENTATION);
            } else {
                mv.gotoPoint(dest, GotoOption.AVANT);
            }
        }

        mv.alignFrontTo(pointEchantillon);

        mv.setVitesse(config.vitesse(0), config.vitesseOrientation());
        rs.enableCalageBordure(TypeCalage.PRISE_ECHANTILLON);
        mv.avanceMM(ECHANTILLON_SIZE / 2.0);

        if (rs.calageCompleted().contains(TypeCalage.PRISE_ECHANTILLON)) {
            task.join();
            if (pousseRouge) {
                mv.avanceMM(10);
            }
            bras.setBrasBas(PositionBras.SOL_PRISE);

            if (bras.waitEnableVentouseBas(couleur)) {
                bras.setBrasBas(PositionBras.SOL_LEVEE); // on lève

                return runAsync(() -> {
                    if (EurobotConfig.ECHANGE_PRISE) {
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
                });
            } else {
                bras.setBrasBas(PositionBras.SOL_LEVEE);
            }

        } else {
            log.warn("Calage de l'échantillon {} non terminé", couleur);
            task.join();
        }

        return CompletableFuture.completedFuture(null);
    }
}
