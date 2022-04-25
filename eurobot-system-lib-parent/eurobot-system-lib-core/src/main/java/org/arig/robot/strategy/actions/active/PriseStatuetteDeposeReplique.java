package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Point;
import org.arig.robot.model.Team;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.model.enums.TypeCalage;
import org.arig.robot.strategy.actions.AbstractEurobotAction;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PriseStatuetteDeposeReplique extends AbstractEurobotAction {

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
        if (!rs.repliqueDepose() && commonServosService.pousseReplique()) {
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
            && ((rs.repliqueDepose() && commonServosService.pousseReplique()) || !commonServosService.pousseReplique())) {
            complete();
        }
    }

    @Override
    public boolean isValid() {
        // TODO : Deux places dans le stock a prendre en compte

        // Valid si la statuette n'as pas été prise
        boolean validStatuette = !rs.statuettePris();

        // Valid si on peut géré la réplique, et non déposée
        boolean validReplique = commonServosService.pousseReplique() && !rs.repliqueDepose();

        // Valid si un des echantillons n'as pas été pris
        boolean validEchantillons = !rs.echantillonAbriChantierCarreFouillePris() || !rs.echantillonAbriChantierDistributeurPris();

        return (validStatuette || validReplique || validEchantillons) && isTimeValid() && remainingTimeValid();
    }

    @Override
    public Point entryPoint() {
        // Coté distributeur
        // getX(372) ; 581 (Jaune = -135° Violet = -45°)

        // Coté fouille
        // getX() ;  (Jaune = -135° Violet = -45°)

        // Statuette (Jaune = 45° Violet = 135°)
        return new Point(getX(435), 435);
    }

    @Override
    public void execute() {
        try {
            Point entry = entryPoint();
            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());

            // TODO : plusieurs point possible
            mv.pathTo(entry);
            mv.gotoOrientationDeg(rs.team() == Team.JAUNE ? 45 : 135);

            if (!rs.echantillonAbriChantierDistributeurPris()) {
                // TODO : Prise et dépose devant l'abri
                group.echantillonAbriChantierDistributeurPris();
            }

            if (!rs.echantillonAbriChantierCarreFouillePris()) {
                // TODO : Prise et dépose devant l'abri
                group.echantillonAbriChantierCarreFouillePris();
            }

            if (!rs.statuettePris() || !rs.repliqueDepose()) {
                if (!rs.statuettePris()) {
                    commonServosService.fourcheStatuettePriseDepose(false);
                }
                if (!rs.repliqueDepose() && commonServosService.pousseReplique()) {
                    commonServosService.langueOuvert(true);
                }

                rs.enableCalageBordure(TypeCalage.ARRIERE, TypeCalage.FORCE);
                mv.setVitesse(robotConfig.vitesse(0), robotConfig.vitesseOrientation());
                mv.reculeMM(200);
                if (!rs.statuettePris()) {
                    commonServosService.fourcheStatuetteFerme(true);
                    // TODO Capteurs pour déposer la statuette
                    group.statuettePris();
                }
                if (!rs.repliqueDepose() && commonServosService.pousseReplique()) {
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
}
