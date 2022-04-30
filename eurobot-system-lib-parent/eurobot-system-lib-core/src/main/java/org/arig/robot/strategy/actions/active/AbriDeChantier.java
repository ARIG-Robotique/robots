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

@Slf4j
@Component
public class AbriDeChantier extends AbstractEurobotAction {

    @Autowired
    private BrasService brasService;

    @Override
    public String name() {
        return EurobotConfig.ACTION_ABRI_CHANTIER;
    }

    @Override
    public int order() {
        boolean stockAbri = rs.stockageAbriChantier();
        int points = 0;
        if (!rs.statuettePris()) {
            points += 5;
        }
        if (commonServosService.pousseReplique() && !rs.repliqueDepose()) {
             points += 10;
        }
        if (!rs.echantillonAbriChantierCarreFouillePris()) {
            points += 1; // 1 point de plus si on prend l'échantillon
            if (!stockAbri) {
                points += 5; // 5 points de plus si on ne prend pas l'échantillon en stock, on le pousse sous l'abri
            }
        }
        if (!rs.echantillonAbriChantierDistributeurPris()) {
            points += 1; // 1 point de plus si on prend l'échantillon
            if (!stockAbri) {
                points += 5; // 5 points de plus si on ne prend pas l'échantillon en stock, on le pousse sous l'abri
            }
        }
        return points + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public void refreshCompleted() {
        if (rs.statuettePris() && rs.echantillonAbriChantierDistributeurPris() && rs.echantillonAbriChantierCarreFouillePris()
            && ((commonServosService.pousseReplique() && rs.repliqueDepose()) || !commonServosService.pousseReplique())) {
            complete();
        }
    }

    @Override
    public boolean isValid() {
        boolean stockAbri = rs.stockageAbriChantier();
        int nbEchantillon = !rs.echantillonAbriChantierDistributeurPris() ? 1 : 0;
        nbEchantillon += !rs.echantillonAbriChantierCarreFouillePris() ? 1 : 0;

        // Valid si la statuette n'as pas été prise
        boolean validStatuette = !rs.statuettePris();

        // Valid si on peut gérer la réplique, et qu'elle n'est pas déposée
        boolean validReplique = commonServosService.pousseReplique() && !rs.repliqueDepose();

        // Valid si un des échantillons n'as pas été pris
        boolean validEchantillons = nbEchantillon > 0;

        // Validitité du stock si on a activé le stockage avec des échantillons
        boolean validStock = rs.stockDisponible() >= nbEchantillon;

        // Si on stock, il faut valid stock et echantillons

        return (validStatuette || validReplique || (!stockAbri && validEchantillons) || (stockAbri && validEchantillons && validStock))
                && isTimeValid() && remainingTimeValid();
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
        return new Point(getX(435), 435);
    }

    @Override
    public void execute() {
        boolean stockAbri = rs.stockageAbriChantier();

        try {
            if (!rs.echantillonAbriChantierDistributeurPris()) {
                mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
                mv.pathTo(entryEchantillonDistributeur());
                mv.gotoOrientationDeg(rs.team() == Team.JAUNE ? -135 : -45);
                processingPriseBordureSafe(CouleurEchantillon.ROCHER_BLEU, group::echantillonAbriChantierDistributeurPris, stockAbri);
            }

            if (!rs.echantillonAbriChantierCarreFouillePris()) {
                mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
                mv.pathTo(entryEchantillonCarreFouille());
                mv.gotoOrientationDeg(rs.team() == Team.JAUNE ? -135 : -45);
                processingPriseBordureSafe(CouleurEchantillon.ROCHER_ROUGE, group::echantillonAbriChantierCarreFouillePris, stockAbri);
            }

            if (!rs.statuettePris() || (commonServosService.pousseReplique() &&!rs.repliqueDepose())) {
                mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
                mv.pathTo(entryStatuette());
                mv.gotoOrientationDeg(rs.team() == Team.JAUNE ? 45 : 135);

                if (!rs.statuettePris()) {
                    commonServosService.fourcheStatuettePriseDepose(false);
                }

                // Si on a pas fait le stockage des échantillons, on les pousses sous l'abri.
                if (!stockAbri) {
                    mv.avanceMM(150);
                    commonServosService.groupeArriereOuvert(true);
                }

                // Ouverture de la langue pour la réplique
                if (commonServosService.pousseReplique() && !rs.repliqueDepose()) {
                    commonServosService.langueOuvert(true);
                }

                rs.enableCalageBordure(TypeCalage.ARRIERE, TypeCalage.FORCE);
                mv.setVitesse(robotConfig.vitesse(0), robotConfig.vitesseOrientation());
                mv.reculeMM(200 + (!stockAbri ? 150 : 0));

                if (!stockAbri){
                    log.info("Normalement on a poussé deux échantillons sous l'abri");
                    group.deposeAbriChantier(CouleurEchantillon.ROCHER_ROUGE, CouleurEchantillon.ROCHER_BLEU);
                }

                if (!rs.statuettePris()) {
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
            commonServosService.fourcheStatuetteFerme(false);
            commonServosService.groupeArriereFerme(false);
            refreshCompleted();
        }
    }

    private void processingPriseBordureSafe(CouleurEchantillon couleur, Runnable notify, boolean stockAbri) throws AvoidingException {
        if (brasService.initPrise(BrasService.TypePrise.BORDURE)) {
            log.info("Init de la prise de l'échantillon abri distributeur OK");
            rs.enableCalageBordure(TypeCalage.AVANT_HAUT, TypeCalage.FORCE);
            mv.setVitesse(robotConfig.vitesse(10), robotConfig.vitesseOrientation());
            mv.avanceMM(150);
            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());

            if (brasService.processPrise(BrasService.TypePrise.BORDURE)) {
                log.info("Prise de l'échantillon OK");
                notify.run();

                if (stockAbri) {
                    // Stockage du contenu de l'abri
                    log.info("Stockage de l'échantillon");
                    brasService.stockagePrise(BrasService.TypePrise.BORDURE, couleur);

                } else {
                    // On pose au sol l'échantillon pour le pousser.
                    log.info("Dépose pour le pousser sous l'abri");
                    brasService.setBrasBas(PositionBras.SOL_PRISE);
                    commonIOService.releasePompeVentouseBas();
                }
            }
        }
        brasService.finalizePrise();
    }
}
