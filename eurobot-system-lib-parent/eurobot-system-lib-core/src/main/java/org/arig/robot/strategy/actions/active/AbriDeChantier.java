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
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Component
public class AbriDeChantier extends AbstractEurobotAction {

    private int nbEchantillons = 0;
    private int nbEchantillonsRemaining = 0;

    boolean firstAction = false;

    @Autowired
    private BrasService bras;

    @Autowired
    private ThreadPoolExecutor executor;

    @Override
    public String name() {
        return EurobotConfig.ACTION_ABRI_CHANTIER;
    }

    @Override
    public int order() {
        if (rs.strategy() == Strategy.BASIC && rs.twoRobots() && (robotName.id() == RobotName.RobotIdentification.ODIN)) {
            // Si c'est Odin et que la strat est la basique avec deux robots
            // C'est la première action
            firstAction = true;
            return 1000;
        }

        refreshConditions();

        int points = 0;
        if (!rs.statuettePrise()) {
            points += 5;
        }
        if (servos.pousseReplique() && !rs.repliqueDepose()) {
            points += 10;
        }
        if (!rs.echantillonAbriChantierCarreFouillePris()) {
            points += 1; // 1 point de plus si on prend l'échantillon
            points += 5; // 5 points de plus si on le pousse sous l'abri
        }
        if (!rs.echantillonAbriChantierDistributeurPris()) {
            points += 1; // 1 point de plus si on prend l'échantillon
            points += 5; // 5 points de plus si on le pousse sous l'abri
        }
        return points + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public void refreshCompleted() {
        if (rs.statuettePrise() && rs.echantillonAbriChantierDistributeurPris() && rs.echantillonAbriChantierCarreFouillePris()
                && ((servos.pousseReplique() && rs.repliqueDepose()) || !servos.pousseReplique())) {
            complete();
        }
    }

    @Override
    public boolean isValid() {
        refreshConditions();

        // Valid si la statuette n'as pas été prise
        boolean validStatuette = !rs.statuettePrise();

        // Valid si on peut gérer la réplique, et qu'elle n'est pas déposée
        boolean validReplique = servos.pousseReplique() && !rs.repliqueDepose();

        // Valid si un des échantillons n'as pas été pris
        boolean validEchantillons = nbEchantillons > 0;

        // Si on stock, il faut valid stock et echantillons
        return (validStatuette || validReplique || validEchantillons)
                && isTimeValid() && remainingTimeBeforeRetourSiteValid();
    }

    @Override
    public Point entryPoint() {
        if (!rs.echantillonAbriChantierDistributeurPris()) {
            return entryEchantillonDistributeur();
        } else if (!rs.echantillonAbriChantierCarreFouillePris()) {
            return entryEchantillonCarreFouille();
        }
        return entryStatuette();
    }

    private Point entryEchantillonDistributeur() {
        // Coté distributeur
        // getX(372) ; 581 (Jaune = -135° Violet = -45°)
        return new Point(getX(345), 537);
    }

    private Point entryEchantillonCarreFouille() {
        // Coté fouille
        // getX() ;  (Jaune = -135° Violet = -45°)
        return new Point(getX(537), 345);
    }

    private Point entryStatuette() {
        // Statuette (Jaune = 45° Violet = 135°)
        return new Point(getX(450), 450);
    }

    private void refreshConditions() {
        nbEchantillons = !rs.echantillonAbriChantierDistributeurPris() ? 1 : 0;
        nbEchantillons += !rs.echantillonAbriChantierCarreFouillePris() ? 1 : 0;
    }

    enum Ventouse {
        HAUT,
        BAS
    }

    @Override
    public void execute() {
        refreshConditions();
        nbEchantillonsRemaining = nbEchantillons;

        try {
            boolean priseHaut = false;
            boolean priseBas = false;
            CompletableFuture<Ventouse> task = null;

            if (!rs.echantillonAbriChantierDistributeurPris()) {
                mv.setVitesse(config.vitesse(), config.vitesseOrientation());
                if (firstAction){
                    firstAction = false;
                    mv.gotoPoint(entryEchantillonDistributeur(), GotoOption.SANS_ORIENTATION);
                } else {
                    mv.pathTo(entryEchantillonDistributeur());
                }
                mv.gotoOrientationDeg(rs.team() == Team.JAUNE ? -135 : -45);
                task = processingPriseBordureSafe(CouleurEchantillon.ROCHER_BLEU, group::echantillonAbriChantierDistributeurPris);
            }

            if (!rs.echantillonAbriChantierCarreFouillePris()) {
                mv.setVitesse(config.vitesse(), config.vitesseOrientation());
                mv.pathTo(entryEchantillonCarreFouille());
                mv.gotoOrientationDeg(rs.team() == Team.JAUNE ? -135 : -45);
                if (task != null) {
                    Ventouse result = task.get();
                    priseBas = result == Ventouse.BAS;
                    priseHaut = result == Ventouse.HAUT;
                }
                task = processingPriseBordureSafe(CouleurEchantillon.ROCHER_ROUGE, group::echantillonAbriChantierCarreFouillePris);
            }

            mv.setVitesse(config.vitesse(), config.vitesseOrientation());
            mv.pathTo(entryStatuette());
            if (task != null) {
                Ventouse result = task.get();
                priseBas = priseBas || result == Ventouse.BAS;
                priseHaut = priseHaut || result == Ventouse.HAUT;
            }

            // Si on a pas mis les échantillons en stock, on les déposent pour les pousser
            if (priseBas || priseHaut) {
                log.info("Dépose devant l'abri");

                // Premier échantillon
                if (priseBas) {
                    log.info("Dépose devant l'abri du premier echantillon récupéré");
                    mv.gotoOrientationDeg(rs.team() == Team.JAUNE ? -165 : -20);
                    bras.setBrasBas(PositionBras.SOL_DEPOSE_1);
                    io.releasePompeVentouseBas();
                    rs.ventouseBas(null);
                    ThreadUtils.waitUntil(() -> !io.presenceVentouseBas(), config.i2cReadTimeMs(), config.timeoutPompe());
                    bras.setBrasBas(PositionBras.STOCK_ENTREE);
                } else {
                    log.info("Rien dans la ventouse bas");
                }
                if (priseHaut) {
                    log.info("Dépose devant l'abri du second échantillon récupéré");
                    mv.gotoOrientationDeg(rs.team() == Team.JAUNE ? -105 : -75);
                    bras.setBrasBas(PositionBras.HORIZONTAL);
                    bras.setBrasHaut(PositionBras.ECHANGE);
                    bras.setBrasBas(PositionBras.ECHANGE);
                    io.enablePompeVentouseBas();
                    io.releasePompeVentouseHaut();
                    rs.ventouseBas(rs.ventouseHaut() != null ? rs.ventouseHaut().getReverseColor() : null);
                    rs.ventouseHaut(null);
                    ThreadUtils.waitUntil(io::presenceVentouseBas, config.i2cReadTimeMs(), config.timeoutPompe());
                    bras.setBrasHaut(PositionBras.HORIZONTAL);
                    bras.setBrasBas(PositionBras.SOL_DEPOSE_1);
                    io.releasePompeVentouseBas();
                    rs.ventouseBas(null);
                    ThreadUtils.waitUntil(() -> !io.presenceVentouseBas(), config.i2cReadTimeMs(), config.timeoutPompe());
                } else {
                    log.info("Rien dans la ventouse haut");
                }
                bras.safeHoming();
                mv.gotoOrientationDeg(rs.team() == Team.JAUNE ? -135 : -45);
                mv.reculeMM(100);
            }

            mv.gotoOrientationDeg(rs.team() == Team.JAUNE ? 45 : 135);

            if (!rs.statuettePrise()) {
                servos.fourcheStatuettePriseDepose(false);
            }

            // On les pousse sous l'abri
            if (priseBas || priseHaut) {
                servos.langueOuvert(false);
                servos.groupeMoustachePoussette(true);
            }

            // Ouverture de la langue pour la réplique
            if (servos.pousseReplique() && !rs.repliqueDepose()) {
                servos.langueOuvert(true);
            }

            rs.enableCalageBordure(TypeCalage.ARRIERE, TypeCalage.FORCE);
            mv.setVitesse(config.vitesse(), config.vitesseOrientation());
            mv.gotoPoint(tableUtils.eloigner(new Point(getX(255), 255), -config.distanceCalageArriere() - 30), GotoOption.ARRIERE);

            rs.enableCalageBordure(TypeCalage.ARRIERE, TypeCalage.FORCE);
            mv.setVitesse(config.vitesse(10), config.vitesseOrientation());
            mv.reculeMMSansAngle(100);

            if ((priseBas || priseHaut) && (rs.calageCompleted().contains(TypeCalage.ARRIERE) || rs.calageCompleted().contains(TypeCalage.FORCE))) {
                log.info("Normalement on a poussé deux échantillons sous l'abri");
                // FIXME pas sur que ça fonctionne du tout s'il n'y en a qu'un
                group.deposeAbriChantier(
                        priseBas ? CouleurEchantillon.ROCHER_ROUGE : null,
                        priseHaut ? CouleurEchantillon.ROCHER_BLEU : null
                );
            }

            if (!rs.statuettePrise()) {
                int nbTry = 5;
                do {
                    servos.fourcheStatuetteFerme(true);
                    if (ThreadUtils.waitUntil(() -> io.presenceStatuette(true), 100, 1000)) {
                        log.info("Youpi ! On a trouvé la statuette");
                        group.statuettePris();
                        break;
                    }
                    servos.fourcheStatuetteVibration(true);
                } while (nbTry-- > 0);
            }
            if (servos.pousseReplique() && !rs.repliqueDepose()) {
                servos.pousseRepliquePoussette(true);
                group.repliqueDepose();
                servos.pousseRepliqueFerme(false);
            }

            mv.setVitesse(config.vitesse(), config.vitesseOrientation());
            mv.avanceMM(100);

        } catch (NoPathFoundException | AvoidingException | ExecutionException | InterruptedException e) {
            log.error("Erreur d'exécution de l'action : {}", e.toString());
            updateValidTime();
        } finally {
            io.releasePompeVentouseHaut();
            io.releasePompeVentouseBas();
            bras.safeHoming();
            servos.fourcheStatuetteFerme(false);
            servos.groupeArriereFerme(false);
            refreshCompleted();
        }
    }

    private CompletableFuture<Ventouse> processingPriseBordureSafe(CouleurEchantillon couleur, Runnable notify) throws AvoidingException, ExecutionException, InterruptedException {
        if (bras.initPrise(BrasService.TypePrise.BORDURE, true).get()) {
            log.info("Init de la prise de l'échantillon abri distributeur OK");
            rs.enableCalageBordure(TypeCalage.AVANT_HAUT, TypeCalage.FORCE);
            mv.setVitesse(config.vitesse(10), config.vitesseOrientation());
            mv.avanceMM(150);
            mv.setVitesse(config.vitesse(), config.vitesseOrientation());

            if (bras.processPrise(BrasService.TypePrise.BORDURE).get()) {
                log.info("Prise de l'échantillon OK");
                rs.ventouseBas(couleur);
                notify.run();

                // Si il en reste deux a prendre, on stock le premier en haut
                if (nbEchantillonsRemaining == 2) {
                    return CompletableFuture.supplyAsync(() -> {
                        log.info("Echange de l'échantillon sur le bras du haut");
                        bras.setBrasBas(PositionBras.ECHANGE_2);
                        bras.setBrasHaut(PositionBras.ECHANGE);

                        io.enablePompeVentouseHaut();
                        io.releasePompeVentouseBas();
                        rs.ventouseHaut(rs.ventouseBas() != null ? rs.ventouseBas().getReverseColor() : null);
                        rs.ventouseBas(null);
                        boolean ok = ThreadUtils.waitUntil(io::presenceVentouseHaut, config.i2cReadTimeMs(), config.timeoutPompe());
                        nbEchantillonsRemaining--;
                        bras.setBrasBas(PositionBras.HORIZONTAL);
                        bras.setBrasHaut(PositionBras.HORIZONTAL);
                        bras.setBrasBas(PositionBras.STOCK_ENTREE);
                        return ok ? Ventouse.HAUT : null;
                    }, executor);

                } else if (nbEchantillonsRemaining == 1) {
                    log.info("Conservation de l'échantillon dans le bras du bas");
                    nbEchantillonsRemaining--;
                    return CompletableFuture.completedFuture(Ventouse.BAS);

                } else {
                    log.warn("Comment c'est possible ?? Nombre d'échantillons de l'abri restant invalide");
                }
            }
        }

        return CompletableFuture.completedFuture(null);
    }
}
