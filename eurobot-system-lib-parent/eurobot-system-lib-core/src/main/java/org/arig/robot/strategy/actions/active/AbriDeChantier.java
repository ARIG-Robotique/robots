package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.CouleurEchantillon;
import org.arig.robot.model.Point;
import org.arig.robot.model.RobotName;
import org.arig.robot.model.Strategy;
import org.arig.robot.model.Team;
import org.arig.robot.model.bras.PositionBras;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.model.enums.TypeCalage;
import org.arig.robot.services.BrasService;
import org.arig.robot.strategy.actions.AbstractEurobotAction;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.Rectangle;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class AbriDeChantier extends AbstractEurobotAction {

    private static final int ORIENT_JAUNE_FACE_ABRI = -135;
    private static final int ORIENT_JAUNE_DOS_ABRI = 45;
    private static final int ORIENT_VIOLET_FACE_ABRI = -45;
    private static final int ORIENT_VIOLET_DOS_ABRI = 135;
    private static final int OFFSET_DEPOSE_2ND_DEG = 45;
    private static final int OFFSET_CENTRAGE_DEG = 15;

    private static final int FOURCHE_WIDTH = 70;

    private int nbEchantillons = 0;
    private int nbEchantillonsRemaining = 0;

    boolean firstAction = false;

    @Autowired
    private BrasService bras;

    @Override
    public List<String> blockingActions() {
        return Arrays.asList(
                EurobotConfig.ACTION_DECOUVERTE_CARRE_FOUILLE,
                EurobotConfig.ACTION_PRISE_DISTRIB_EQUIPE
        );
    }

    @Override
    public Rectangle blockingZone() {
        if (rs.team() == Team.JAUNE) {
            return new Rectangle(0, 0, 700, 850);
        } else {
            return new Rectangle(2300, 0, 700, 850);
        }
    }

    @Override
    public String name() {
        return EurobotConfig.ACTION_ABRI_CHANTIER;
    }

    @Override
    public int executionTimeMs() {
        int executionTime = 0;
        if (!rs.echantillonAbriChantierDistributeurPris() || !rs.echantillonAbriChantierCarreFouillePris()) {
            executionTime += 15000; // Cycle prise + dépose
        }

        if (!rs.statuettePrise() || (!rs.repliqueDepose() && servos.pousseReplique())) {
            executionTime += 6000;
        }

        return executionTime;
    }

    @Override
    public int order() {
        if ((rs.strategy() == Strategy.BASIC || rs.strategy() == Strategy.FINALE_2)
                && rs.twoRobots() && (robotName.id() == RobotName.RobotIdentification.ODIN)) {
            firstAction = true;
            return 500;
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
        if (rs.stockTaille() > 0) {
            points += 5;
        }
        return points + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public void refreshCompleted() {
        if ((rs.statuettePrise() || !rs.statuettePrise() && rs.repliqueDepose())
                && rs.echantillonAbriChantierDistributeurPris() && rs.echantillonAbriChantierCarreFouillePris()
                && ((servos.pousseReplique() && rs.repliqueDepose()) || !servos.pousseReplique())) {
            complete();
        }
    }

    @Override
    public boolean isValid() {
        refreshConditions();

        // Valid si la statuette n'as pas été prise
        boolean validStatuette = !rs.statuettePrise() && !io.presenceStatuette(false) && !rs.repliqueDepose();

        // Valid si on peut gérer la réplique, et qu'elle n'est pas déposée
        boolean validReplique = servos.pousseReplique() && !rs.repliqueDepose();

        // Valid si un des échantillons n'as pas été pris
        boolean validEchantillons = nbEchantillons > 0;

        // Si on stock, il faut valid stock et echantillons
        return (validStatuette || validReplique || validEchantillons)
                && isTimeValid() && timeBeforeRetourValid();
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
        return new Point(getX(460), 460);
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
            boolean needPath = !firstAction;

            boolean priseHaut = false;
            boolean priseBas = false;
            CouleurEchantillon troisiemeDepose = null;
            CompletableFuture<Ventouse> task = null;

            // prise du premier échantillon
            if (!rs.echantillonAbriChantierDistributeurPris()) {
                mv.setVitesse(config.vitesse(), config.vitesseOrientation());
                if (needPath) {
                    needPath = false;
                    mv.pathTo(entryEchantillonDistributeur());
                } else {
                    rs.enableAvoidance();
                    mv.gotoPoint(entryEchantillonDistributeur());
                }
                mv.gotoOrientationDeg(rs.team() == Team.JAUNE ? ORIENT_JAUNE_FACE_ABRI : ORIENT_VIOLET_FACE_ABRI);
                task = processingPrise(CouleurEchantillon.ROCHER_BLEU, group::echantillonAbriChantierDistributeurPris);
            }

            rs.enableAvoidance(true);

            // prise du second échantillon
            if (!rs.echantillonAbriChantierCarreFouillePris()) {
                mv.setVitesse(config.vitesse(), config.vitesseOrientation());
                if (needPath) {
                    needPath = false;
                    mv.pathTo(entryEchantillonCarreFouille());
                } else {
                    mv.gotoPoint(entryEchantillonCarreFouille());
                }
                mv.gotoOrientationDeg(rs.team() == Team.JAUNE ? ORIENT_JAUNE_FACE_ABRI : ORIENT_VIOLET_FACE_ABRI);
                if (task != null) {
                    Ventouse result = task.join();
                    priseBas = result == Ventouse.BAS;
                    priseHaut = result == Ventouse.HAUT;
                }
                task = processingPrise(CouleurEchantillon.ROCHER_ROUGE, group::echantillonAbriChantierCarreFouillePris);
            }

            // point d'entrée principal
            rs.enableAvoidance(true);
            mv.setVitesse(config.vitesse(), config.vitesseOrientation());
            if (needPath) {
                mv.pathTo(entryStatuette());
            } else {
                mv.gotoPoint(entryStatuette());
            }
            if (task != null) {
                Ventouse result = task.join();
                priseBas = priseBas || result == Ventouse.BAS;
                priseHaut = priseHaut || result == Ventouse.HAUT;
            }

            // Si on a pas mis les échantillons en stock, on les dépose pour les pousser
            if (priseBas || priseHaut) {
                log.info("Dépose devant l'abri");

                if (priseBas) {
                    log.info("Dépose devant l'abri du premier echantillon récupéré");
                    mv.gotoOrientationDeg(rs.team() == Team.JAUNE ? ORIENT_JAUNE_FACE_ABRI : ORIENT_VIOLET_FACE_ABRI);
                    bras.setBrasBas(PositionBras.SOL_DEPOSE_1);
                    bras.waitReleaseVentouseBas();
                    bras.setBrasBas(PositionBras.STOCK_ENTREE);
                } else {
                    log.info("Rien dans la ventouse bas");
                }

                if (rs.stockTaille() > 0) {
                    log.info("Dépose devant l'abri d'un échantillon supplémentaire");
                    if (bras.destockageBas()) {
                        if (priseBas) {
                            mv.reculeMM(EurobotConfig.ECHANTILLON_SIZE + 10);
                        }
                        bras.setBrasBas(PositionBras.SOL_DEPOSE_1);
                        troisiemeDepose = rs.ventouseBas();
                        bras.waitReleaseVentouseBas();
                        bras.setBrasBas(PositionBras.STOCK_ENTREE);
                    }
                }

                if (priseHaut) {
                    log.info("Dépose devant l'abri du second échantillon récupéré");
                    if (priseBas || troisiemeDepose != null) {
                        // On a mis en face du piedestal un premier echantillon
                        mv.gotoOrientationDeg(rs.team() == Team.JAUNE ? ORIENT_JAUNE_FACE_ABRI + OFFSET_DEPOSE_2ND_DEG : ORIENT_VIOLET_FACE_ABRI - OFFSET_DEPOSE_2ND_DEG);
                    } else {
                        // Va savoir pourquoi, on a pas mis de premier echantillon (deuxième prise planté ??), mais on en a un en haut
                        // On le pose comme le premier
                        mv.gotoOrientationDeg(rs.team() == Team.JAUNE ? ORIENT_JAUNE_FACE_ABRI : ORIENT_VIOLET_FACE_ABRI);
                    }
                    bras.echangeHautBas();
                    bras.setBrasHaut(PositionBras.HORIZONTAL);
                    bras.setBrasBas(PositionBras.SOL_DEPOSE_1);
                    if (priseBas || troisiemeDepose != null) {
                        // On tourne pour aligner les deux échantillons
                        mv.setRampeOrientation(config.rampeAccelOrientation(), config.rampeDecelOrientation(20));
                        mv.gotoOrientationDeg(rs.team() == Team.JAUNE ? ORIENT_JAUNE_FACE_ABRI + OFFSET_CENTRAGE_DEG : ORIENT_VIOLET_FACE_ABRI - OFFSET_CENTRAGE_DEG);
                        mv.setRampeOrientation(config.rampeAccelOrientation(), config.rampeDecelOrientation());
                    }
                    bras.waitReleaseVentouseBas();
                } else {
                    log.info("Rien dans la ventouse haut");
                }

                CompletableFuture<Void> asyncHoming = runAsync(bras::repos);
                mv.gotoOrientationDeg(rs.team() == Team.JAUNE ? ORIENT_JAUNE_FACE_ABRI : ORIENT_VIOLET_FACE_ABRI);
                mv.reculeMM(90);
                asyncHoming.join();
            }

            mv.gotoOrientationDeg(rs.team() == Team.JAUNE ? ORIENT_JAUNE_DOS_ABRI : ORIENT_VIOLET_DOS_ABRI);
            if (!rs.statuettePrise() && !io.presenceStatuette(false)) {
                servos.fourcheStatuettePriseDepose(false);
            }

            // On les poussent sous l'abri
            if (priseBas || priseHaut) {
                servos.groupeArriereOuvert(true);
            }

            // Ouverture de la langue pour la réplique
            if (servos.pousseReplique() && !rs.repliqueDepose()) {
                servos.langueOuvert(true);
            }

            rs.disableAvoidance();

            rs.enableCalageBordure(TypeCalage.ARRIERE, TypeCalage.FORCE);
            mv.setVitesse(config.vitesse(), config.vitesseOrientation());
            mv.gotoPoint(tableUtils.eloigner(new Point(getX(255), 255), -config.distanceCalageArriere() - FOURCHE_WIDTH), GotoOption.ARRIERE);

            rs.enableCalageBordure(TypeCalage.ARRIERE, TypeCalage.FORCE);
            mv.setVitesse(config.vitesse(10), config.vitesseOrientation());
            mv.reculeMMSansAngle(2 * FOURCHE_WIDTH);

            if ((priseBas || priseHaut) && (rs.calageCompleted().contains(TypeCalage.ARRIERE) || rs.calageCompleted().contains(TypeCalage.FORCE))) {
                servos.groupeMoustachePoussette(true); // Histoire de les pousser sous l'abri un peu plus loin

                log.info("Normalement on a poussé {} échantillons sous l'abri", troisiemeDepose == null ? "deux" : "trois");
                group.deposeAbriChantier(
                        priseBas ? CouleurEchantillon.ROCHER_ROUGE : null,
                        priseHaut ? CouleurEchantillon.ROCHER_BLEU : null,
                        troisiemeDepose
                );
            }

            if (!rs.statuettePrise()) {
                int nbTry = 5;
                do {
                    servos.fourcheStatuetteFerme(true);
                    if (ThreadUtils.waitUntil(() -> io.presenceStatuette(true), 100, 500)) {
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

            complete(true);

            mv.setVitesse(config.vitesse(), config.vitesseOrientation());
            mv.avanceMM(100);

        } catch (NoPathFoundException | AvoidingException e) {
            log.error("Erreur d'exécution de l'action : {}", e.toString());
            updateValidTime();
            bras.safeHoming();

        } finally {
            firstAction = false;
            servos.fourcheStatuetteFerme(false);
            servos.groupeArriereFerme(false);
            refreshCompleted();
        }
    }

    private CompletableFuture<Ventouse> processingPrise(CouleurEchantillon couleur, Runnable notify) throws AvoidingException {
        bras.setBrasHaut(PositionBras.HORIZONTAL);
        bras.setBrasBas(PositionBras.BORDURE_APPROCHE);

        rs.enableCalageBordure(TypeCalage.AVANT_HAUT, TypeCalage.FORCE);
        rs.disableAvoidance();
        mv.setVitesse(config.vitesse(10), config.vitesseOrientation());
        mv.avanceMM(150);
        mv.setVitesse(config.vitesse(), config.vitesseOrientation());

        bras.setBrasBas(PositionBras.BORDURE_PRISE);
        boolean priseOk = bras.waitEnableVentouseBas(couleur);
        bras.setBrasBas(PositionBras.BORDURE_APPROCHE);

        rs.enableAvoidance(true);
        mv.reculeMM(EurobotConfig.ECHANTILLON_SIZE);

        if (priseOk) {
            log.info("Prise de l'échantillon OK");
            notify.run();

            // Si il en reste deux a prendre, on stock le premier en haut
            if (nbEchantillonsRemaining == 2) {
                return supplyAsync(() -> {
                    boolean ok = bras.echangeBasHaut(true);
                    nbEchantillonsRemaining--;
                    bras.setBrasBas(PositionBras.HORIZONTAL);
                    bras.setBrasHaut(PositionBras.HORIZONTAL);
                    bras.setBrasBas(PositionBras.BORDURE_APPROCHE);
                    return ok ? Ventouse.HAUT : null;
                });

            } else if (nbEchantillonsRemaining == 1) {
                log.info("Conservation de l'échantillon dans le bras du bas");
                nbEchantillonsRemaining--;
                return CompletableFuture.completedFuture(Ventouse.BAS);

            } else {
                log.warn("Comment c'est possible ?? Nombre d'échantillons de l'abri restant invalide");
            }
        }

        return CompletableFuture.completedFuture(null);
    }
}
