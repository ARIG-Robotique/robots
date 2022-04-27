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
public class PriseStatuetteDeposeReplique extends AbstractEurobotAction {

    @Autowired
    private BrasService brasService;

    @Override
    public String name() {
        return EurobotConfig.ACTION_STATUETTE_REPLIQUE;
    }

    @Override
    public int order() {
        int points = 0;
        if (!rs.statuettePris()) {
            points += 5;
        }
        if (commonServosService.pousseReplique() && !rs.repliqueDepose()) {
             points += 10;
        }
        if (!rs.echantillonAbriChantierCarreFouillePris()) {
            points += 1; // 1 point de plus si on prend l'échantillon
        }
        if (!rs.echantillonAbriChantierDistributeurPris()) {
            points += 1; // 1 point de plus si on prend l'échantillon
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
        // Valid si la statuette n'as pas été prise
        boolean validStatuette = !rs.statuettePris();

        // Valid si on peut géré la réplique, et non déposée
        boolean validReplique = commonServosService.pousseReplique() && !rs.repliqueDepose();

        // Valid si un des echantillons n'as pas été pris et qu'il reste de la place en stock
        int nbEchantillon = !rs.echantillonAbriChantierDistributeurPris() ? 1 : 0;
        nbEchantillon += !rs.echantillonAbriChantierCarreFouillePris() ? 1 : 0;
        boolean validEchantillons = nbEchantillon > 0 && rs.stockDisponible() >= nbEchantillon;

        return (validStatuette || validReplique || validEchantillons) && isTimeValid() && remainingTimeValid();
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
        try {
            if (!rs.echantillonAbriChantierDistributeurPris()) {
                mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
                mv.pathTo(entryEchantillonDistributeur());
                mv.gotoOrientationDeg(rs.team() == Team.JAUNE ? -135 : -45);
                processingPriseBordureSafe(CouleurEchantillon.ROCHER_BLEU, group::echantillonAbriChantierDistributeurPris);
            }

            if (!rs.echantillonAbriChantierCarreFouillePris()) {
                mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
                mv.pathTo(entryEchantillonCarreFouille());
                mv.gotoOrientationDeg(rs.team() == Team.JAUNE ? -135 : -45);
                processingPriseBordureSafe(CouleurEchantillon.ROCHER_ROUGE, group::echantillonAbriChantierCarreFouillePris);
            }

            if (!rs.statuettePris() || (commonServosService.pousseReplique() &&!rs.repliqueDepose())) {
                mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
                mv.pathTo(entryStatuette());
                mv.gotoOrientationDeg(rs.team() == Team.JAUNE ? 45 : 135);
                if (!rs.statuettePris()) {
                    commonServosService.fourcheStatuettePriseDepose(false);
                }
                if (commonServosService.pousseReplique() && !rs.repliqueDepose()) {
                    commonServosService.langueOuvert(true);
                }

                rs.enableCalageBordure(TypeCalage.ARRIERE, TypeCalage.FORCE);
                mv.setVitesse(robotConfig.vitesse(0), robotConfig.vitesseOrientation());
                mv.reculeMM(200);
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
            if (commonServosService.pousseReplique()) {
                commonServosService.langueFerme(false);
            }
            refreshCompleted();
        }
    }

    private void processingPriseBordureSafe(CouleurEchantillon couleur, Runnable notify) throws AvoidingException {
        if (brasService.initPrise(BrasService.TypePrise.BORDURE)) {
            log.info("Init de la prise de l'échantillon abri distributeur OK");
            rs.enableCalageBordure(TypeCalage.FORCE);
            mv.setVitesse(robotConfig.vitesse(10), robotConfig.vitesseOrientation());
            mv.avanceMM(150);
            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());

            if (brasService.processPrise(BrasService.TypePrise.BORDURE)) {
                log.info("Prise de l'achantillon OK");
                notify.run();

                // On pose au sol l'échantillon pour se recaler dessus.
                brasService.setBrasBas(PositionBras.SOL_PRISE);
                commonIOService.releasePompeVentouseBas();
                brasService.setBrasBas(PositionBras.SOL_DEPOSE);

                rs.enableCalageBordure(TypeCalage.PRISE_ECHANTILLON);
                mv.setVitesse(robotConfig.vitesse(0), robotConfig.vitesseOrientation());
                mv.avanceMM(70);
                brasService.setBrasBas(PositionBras.SOL_PRISE);
                if (brasService.processPrise(BrasService.TypePrise.SOL)) {
                    log.info("Reprise de l'échantillon abri distributeur au sol OK");
                    brasService.stockagePrise(BrasService.TypePrise.SOL, couleur);
                }
            }
        }
        brasService.finalizePrise();
    }
}
