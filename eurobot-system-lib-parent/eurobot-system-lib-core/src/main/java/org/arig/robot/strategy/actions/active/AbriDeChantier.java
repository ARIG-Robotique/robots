package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.CouleurEchantillon;
import org.arig.robot.model.Point;
import org.arig.robot.model.Team;
import org.arig.robot.model.bras.PositionBras;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.model.enums.TypeCalage;
import org.arig.robot.services.BrasService;
import org.arig.robot.strategy.actions.AbstractEurobotAction;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AbriDeChantier extends AbstractEurobotAction {

    private boolean stockEchantillons = false;
    private int nbEchantillons = 0;
    private int nbEchantillonsRemaining = 0;

    @Autowired
    private BrasService brasService;

    @Override
    public String name() {
        return EurobotConfig.ACTION_ABRI_CHANTIER;
    }

    @Override
    public int order() {
        refreshConditions();

        int points = 0;
        if (!rs.statuettePrise()) {
            points += 5;
        }
        if (commonServosService.pousseReplique() && !rs.repliqueDepose()) {
             points += 10;
        }
        if (!rs.echantillonAbriChantierCarreFouillePris()) {
            points += 1; // 1 point de plus si on prend l'échantillon
            if (!stockEchantillons) {
                points += 5; // 5 points de plus si on ne prend pas l'échantillon en stock, on le pousse sous l'abri
            }
        }
        if (!rs.echantillonAbriChantierDistributeurPris()) {
            points += 1; // 1 point de plus si on prend l'échantillon
            if (!stockEchantillons) {
                points += 5; // 5 points de plus si on ne prend pas l'échantillon en stock, on le pousse sous l'abri
            }
        }
        return points + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public void refreshCompleted() {
        if (rs.statuettePrise() && rs.echantillonAbriChantierDistributeurPris() && rs.echantillonAbriChantierCarreFouillePris()
            && ((commonServosService.pousseReplique() && rs.repliqueDepose()) || !commonServosService.pousseReplique())) {
            complete();
        }
    }

    @Override
    public boolean isValid() {
        refreshConditions();

        // Valid si la statuette n'as pas été prise
        boolean validStatuette = !rs.statuettePrise();

        // Valid si on peut gérer la réplique, et qu'elle n'est pas déposée
        boolean validReplique = commonServosService.pousseReplique() && !rs.repliqueDepose();

        // Valid si un des échantillons n'as pas été pris
        boolean validEchantillons = nbEchantillons > 0;

        // Si on stock, il faut valid stock et echantillons
        return (validStatuette || validReplique || validEchantillons)
                && isTimeValid() && remainingTimeValid() && rs.getRemainingTime() < 60000;
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
        stockEchantillons = rs.stockageAbriChantier() && rs.stockDisponible() >= 2;
        nbEchantillons = !rs.echantillonAbriChantierDistributeurPris() ? 1 : 0;
        nbEchantillons += !rs.echantillonAbriChantierCarreFouillePris() ? 1 : 0;
    }

    @Override
    public void execute() {
        refreshConditions();
        nbEchantillonsRemaining = nbEchantillons;
        boolean poussetteRequise = false;
        try {
            if (!rs.echantillonAbriChantierDistributeurPris()) {
                mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
                mv.pathTo(entryEchantillonDistributeur());
                mv.gotoOrientationDeg(rs.team() == Team.JAUNE ? -135 : -45);
                processingPriseBordureSafe(CouleurEchantillon.ROCHER_BLEU, group::echantillonAbriChantierDistributeurPris);
                if (!stockEchantillons) {
                    poussetteRequise = true;
                }
            }

            if (!rs.echantillonAbriChantierCarreFouillePris()) {
                mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
                mv.pathTo(entryEchantillonCarreFouille());
                mv.gotoOrientationDeg(rs.team() == Team.JAUNE ? -135 : -45);
                processingPriseBordureSafe(CouleurEchantillon.ROCHER_ROUGE, group::echantillonAbriChantierCarreFouillePris);
                if (!stockEchantillons) {
                    poussetteRequise = true;
                }
            }

            if (poussetteRequise || !rs.statuettePrise()
                    || (commonServosService.pousseReplique() && !rs.repliqueDepose())) {

                mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
                mv.pathTo(entryStatuette());

                // Si on a pas mis les échantillons en stock, on les déposent pour les pousser
                if (poussetteRequise) {
                    // Premier échantillon
                    if (commonIOService.presenceVentouseBas()) {
                        log.info("Dépose devant l'abri du premier echantillon récupérer");
                        mv.gotoOrientationDeg(rs.team() == Team.JAUNE ? -165 : -20);
                        brasService.setBrasBas(PositionBras.SOL_DEPOSE);
                        commonIOService.releasePompeVentouseBas();
                        ThreadUtils.waitUntil(() -> !commonIOService.presenceVentouseBas(), robotConfig.i2cReadTimeMs(), robotConfig.timeoutPompe());
                        brasService.setBrasBas(PositionBras.STOCK_ENTREE);
                    }
                    if (commonIOService.presenceVentouseHaut()) {
                        log.info("Dépose devant l'abri du second échantillon récupérer");
                        mv.gotoOrientationDeg(rs.team() == Team.JAUNE ? -105 : -75);
                        brasService.setBrasBas(PositionBras.HORIZONTAL);
                        brasService.setBrasHaut(PositionBras.ECHANGE);
                        brasService.setBrasBas(PositionBras.ECHANGE);
                        commonIOService.enablePompeVentouseBas();
                        commonIOService.releasePompeVentouseHaut();
                        ThreadUtils.waitUntil(commonIOService::presenceVentouseBas, robotConfig.i2cReadTimeMs(), robotConfig.timeoutPompe());
                        brasService.setBrasHaut(PositionBras.HORIZONTAL);
                        brasService.setBrasBas(PositionBras.SOL_DEPOSE);
                        commonIOService.releasePompeVentouseBas();
                        ThreadUtils.waitUntil(() -> !commonIOService.presenceVentouseBas(), robotConfig.i2cReadTimeMs(), robotConfig.timeoutPompe());
                    }
                    brasService.safeHoming();
                    mv.gotoOrientationDeg(rs.team() == Team.JAUNE ? -135 : -45);
                    mv.reculeMM(100);
                }

                mv.gotoOrientationDeg(rs.team() == Team.JAUNE ? 45 : 135);

                if (!rs.statuettePrise()) {
                    commonServosService.fourcheStatuettePriseDepose(false);
                }

                // Si on a pas fait le stockage des échantillons, on les pousses sous l'abri.
                if (!stockEchantillons) {
                    commonServosService.langueOuvert(false);
                    commonServosService.groupeMoustachePoussette(true);
                }

                // Ouverture de la langue pour la réplique
                if (commonServosService.pousseReplique() && !rs.repliqueDepose()) {
                    commonServosService.langueOuvert(true);
                }

                rs.enableCalageBordure(TypeCalage.ARRIERE, TypeCalage.FORCE);
                mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
                mv.gotoPoint(tableUtils.eloigner(new Point(getX(255), 255), -robotConfig.distanceCalageArriere() - 30), GotoOption.ARRIERE);

                rs.enableCalageBordure(TypeCalage.ARRIERE, TypeCalage.FORCE);
                mv.setVitesse(robotConfig.vitesse(10), robotConfig.vitesseOrientation());
                mv.reculeMMSansAngle(100);

                if (!stockEchantillons && (rs.calageCompleted().contains(TypeCalage.ARRIERE) || rs.calageCompleted().contains(TypeCalage.FORCE))) {
                    log.info("Normalement on a poussé deux échantillons sous l'abri");
                    group.deposeAbriChantier(CouleurEchantillon.ROCHER_ROUGE, CouleurEchantillon.ROCHER_BLEU);
                }

                if (!rs.statuettePrise()) {
                    commonServosService.fourcheStatuetteFerme(true);
                    // TODO Capteur pour vérifier que la prise de la statuette est bien terminé
                    group.statuettePris();
                }
                if (commonServosService.pousseReplique() && !rs.repliqueDepose()) {
                    commonServosService.pousseRepliquePoussette(true);
                    group.repliqueDepose();
                    commonServosService.pousseRepliqueFerme(false);
                }

                mv.avanceMM(100);
            }

        } catch (NoPathFoundException | AvoidingException e) {
            log.error("Erreur d'exécution de l'action : {}", e.toString());
            updateValidTime();
        } finally {
            commonIOService.releasePompeVentouseHaut();
            commonIOService.releasePompeVentouseBas();
            brasService.safeHoming();
            commonServosService.fourcheStatuetteFerme(false);
            commonServosService.groupeArriereFerme(false);
            refreshCompleted();
        }
    }

    private void processingPriseBordureSafe(CouleurEchantillon couleur, Runnable notify) throws AvoidingException {
        if (brasService.initPrise(BrasService.TypePrise.BORDURE, true)) {
            log.info("Init de la prise de l'échantillon abri distributeur OK");
            rs.enableCalageBordure(TypeCalage.AVANT_HAUT, TypeCalage.FORCE);
            mv.setVitesse(robotConfig.vitesse(10), robotConfig.vitesseOrientation());
            mv.avanceMM(150);
            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());

            if (brasService.processPrise(BrasService.TypePrise.BORDURE)) {
                log.info("Prise de l'échantillon OK");
                notify.run();
                if (stockEchantillons) {
                    // Stockage du contenu de l'abri
                    log.info("Stockage de l'échantillon");
                    brasService.stockagePrise(BrasService.TypePrise.BORDURE, couleur);

                } else {
                    // Si il en reste deux a prendre, on stock le premier en haut
                    if (nbEchantillonsRemaining == 2) {
                        log.info("Echange de l'échantillon sur le bras du haut");
                        brasService.setBrasBas(PositionBras.ECHANGE_2);
                        brasService.setBrasHaut(PositionBras.ECHANGE);

                        commonIOService.enablePompeVentouseHaut();
                        commonIOService.releasePompeVentouseBas();
                        ThreadUtils.waitUntil(commonIOService::presenceVentouseHaut, robotConfig.i2cReadTimeMs(), robotConfig.timeoutPompe());
                        nbEchantillonsRemaining--;
                        brasService.setBrasBas(PositionBras.HORIZONTAL);
                        brasService.setBrasHaut(PositionBras.HORIZONTAL);
                        brasService.setBrasBas(PositionBras.STOCK_ENTREE);

                    } else if (nbEchantillonsRemaining == 1) {
                        log.info("Conservation de l'échantillon dans le bras du bas");
                        nbEchantillonsRemaining--;

                    } else {
                        log.warn("Comment c'est possible ?? Nombre d'échantillons de l'abri restant invalide");
                    }
                }
            }
        }
        if (stockEchantillons) {
            brasService.finalizePrise();
        }
    }
}
