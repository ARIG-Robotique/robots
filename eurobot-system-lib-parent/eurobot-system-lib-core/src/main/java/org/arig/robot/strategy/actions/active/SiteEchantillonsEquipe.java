package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.CouleurEchantillon;
import org.arig.robot.model.Point;
import org.arig.robot.model.RobotName;
import org.arig.robot.model.Strategy;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.model.enums.TypeCalage;
import org.arig.robot.services.BrasService;
import org.arig.robot.strategy.actions.AbstractEurobotAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.arig.robot.constants.EurobotConfig.PTS_DEPOSE_PRISE;
import static org.arig.robot.constants.EurobotConfig.ECHANTILLON_SIZE;

@Slf4j
@Component
public class SiteEchantillonsEquipe extends AbstractEurobotAction {

    @Autowired
    private BrasService bras;

    private boolean firstAction = false;
    private CouleurEchantillon echantillonEntry;

    @Override
    public String name() {
        return EurobotConfig.ACTION_PRISE_ECHANTILLONS_SITE_EQUIPE;
    }

    @Override
    public int order() {
        if (rs.strategy() == Strategy.BASIC && (
                (robotName.id() == RobotName.RobotIdentification.NERELL) || (!rs.twoRobots() && robotName.id() == RobotName.RobotIdentification.ODIN)
        )) {
            // Si c'est Nerell et que la strat est la basique.
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
        return isTimeValid() && remainingTimeBeforeRetourSiteValid() && !rs.siteEchantillonPris();
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
            mv.setVitesse(config.vitesse(), config.vitesseOrientation());
            CompletableFuture<Boolean> task = null;
            if (firstAction && echantillonEntry == CouleurEchantillon.ROCHER_VERT) {
                task = priseEchantillon(task, false, false, pointEchantillonVert(), CouleurEchantillon.ROCHER_VERT);
                task = priseEchantillon(task, false, true, pointEchantillonRouge(), CouleurEchantillon.ROCHER_ROUGE);
                task = priseEchantillon(task, false, true, pointEchantillonBleu(), CouleurEchantillon.ROCHER_BLEU);
            } else {
                if (echantillonEntry == CouleurEchantillon.ROCHER_ROUGE) {
                    // De bas en haut
                    task = priseEchantillon(task, true, false, pointEchantillonRouge(), CouleurEchantillon.ROCHER_ROUGE);
                    task = priseEchantillon(task, false, true, pointEchantillonVert(), CouleurEchantillon.ROCHER_VERT);
                    task = priseEchantillon(task, false, true, pointEchantillonBleu(), CouleurEchantillon.ROCHER_BLEU);
                } else {
                    // De haut en bas
                    task = priseEchantillon(task, true, false, pointEchantillonBleu(), CouleurEchantillon.ROCHER_BLEU);
                    task = priseEchantillon(task, false, true, pointEchantillonVert(), CouleurEchantillon.ROCHER_VERT);
                    task = priseEchantillon(task, false, true, pointEchantillonRouge(), CouleurEchantillon.ROCHER_ROUGE);
                }
            }

            task.get();
            bras.finalizePrise();

            group.siteEchantillonPris();

        } catch (NoPathFoundException | AvoidingException | ExecutionException | InterruptedException e) {
            log.error("Erreur d'exécution de l'action : {}", e.toString());
            updateValidTime();
            bras.safeHoming();

        } finally {
            firstAction = false;
            refreshCompleted();
            rs.disableCalageBordure();
        }
    }

    private CompletableFuture<Boolean> priseEchantillon(CompletableFuture<Boolean> previousTask, boolean path, boolean needAlignFront, Point pointEchantillon, CouleurEchantillon couleur) throws AvoidingException, NoPathFoundException, ExecutionException, InterruptedException {
        mv.setVitesse(config.vitesse(), config.vitesseOrientation());
        if (needAlignFront) {
            mv.alignFrontTo(pointEchantillon);
        }
        final Point dest = tableUtils.eloigner(pointEchantillon, -config.distanceCalageAvant() - (ECHANTILLON_SIZE / 3.0));
        if (path) {
            mv.pathTo(dest, GotoOption.AVANT);
        } else {
            if (!needAlignFront) {
                // Avec orientation
                mv.gotoPoint(dest, GotoOption.AVANT);
            } else {
                // Sans orientation, c'est déjà fait
                mv.gotoPoint(dest, GotoOption.AVANT, GotoOption.SANS_ORIENTATION);
            }
        }

        mv.setVitesse(config.vitesse(0), config.vitesseOrientation());
        rs.enableCalageBordure(TypeCalage.PRISE_ECHANTILLON);
        mv.avanceMM(ECHANTILLON_SIZE / 2);
        if (rs.calageCompleted().contains(TypeCalage.PRISE_ECHANTILLON)) {
            mv.avanceMM(20); // Histoire de bien le charger dans le robot
            if (previousTask != null) previousTask.get();

            if (bras.initPrise(BrasService.TypePrise.SOL, true).get()
                    && bras.processPrise(BrasService.TypePrise.SOL).get()) {
                log.info("Echantillon pris : {}", couleur);
                return bras.stockagePrise(BrasService.TypePrise.SOL, couleur, false);
            }
        } else {
            log.warn("Calage de l'échantillon {} non terminé", couleur);
            if (previousTask != null) previousTask.get();
        }

        return CompletableFuture.completedFuture(false);
    }
}
