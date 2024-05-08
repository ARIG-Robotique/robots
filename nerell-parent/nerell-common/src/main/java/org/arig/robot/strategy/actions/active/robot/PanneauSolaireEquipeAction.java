package org.arig.robot.strategy.actions.active.robot;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Point;
import org.arig.robot.model.StockPots;
import org.arig.robot.model.Strategy;
import org.arig.robot.model.Team;
import org.arig.robot.model.bras.PositionBras;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.model.enums.TypeCalage;
import org.arig.robot.strategy.actions.AbstractNerellAction;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import static org.arig.robot.constants.NerellConstantesConfig.VITESSE_ROUE_PANNEAU;

@Slf4j
@Component
public class PanneauSolaireEquipeAction extends AbstractNerellAction implements InitializingBean {
    public static final int ENTRY_X = 210;
    public static final int ENTRY_Y = 210;

    public static final int WORK_Y = 230;

    @Override
    public void afterPropertiesSet() throws Exception {
        rs.panneauxSolaire().triedActionEquipe(false);
    }

    @Override
    public String name() {
        return EurobotConfig.ACTION_PANNEAU_SOLAIRE_EQUIPE;
    }

    @Override
    public int executionTimeMs() {
        return 0; // TODO
    }

    @Override
    public boolean isValid() {
        // TODO 1er interdit si zone de depose pleine
        return isTimeValid()
                && rs.bras().arriereLibre()
                && !rs.panneauxSolaire().equipeDone()
                && !rs.panneauxSolaire().triedActionEquipe();
    }

    @Override
    public int order() {
        return rs.strategy() == Strategy.SUD ? 1000 : 15;
    }

    @Override
    public Point entryPoint() {
        return new Point(getX(ENTRY_X), ENTRY_Y);
    }

    @Override
    public void execute() {

        boolean enZone = false;

        try {
            if (rs.strategy() != Strategy.SUD) {
                final Point entry = entryPoint();

                mv.setVitesse(config.vitesse(), config.vitesseOrientation());
                mv.pathTo(entry);

                // callage Y
                mv.setVitessePercent(60, 100);
                mv.gotoOrientationDeg(90);
                bras.setBrasArriere(PositionBras.CALLAGE_PANNEAUX);
                rs.enableCalageBordure(TypeCalage.ARRIERE, TypeCalage.FORCE);
                mv.reculeMM(ENTRY_Y - config.distanceCalageArriere() - 10);

                if (rs.calageCompleted().contains(TypeCalage.FORCE)) {
                    log.warn("Blocage pendant le callage du panneau");
                    mv.avanceMM(100);
                    runAsync(() -> bras.setBrasArriere(PositionBras.INIT));
                    rs.panneauxSolaire().triedActionEquipe(true);
                    complete();
                    return;
                }

                mv.setVitessePercent(0, 100);
                rs.enableCalageBordure(TypeCalage.ARRIERE);
                mv.reculeMMSansAngle(40);
                checkRecalageYmm(config.distanceCalageArriere(), TypeCalage.ARRIERE);
                checkRecalageAngleDeg(90, TypeCalage.ARRIERE);

                // les déplacements sont relatifs pour rester à la bonne distance de la bordure
                mv.setVitessePercent(60, 100);
                mv.avanceMM(WORK_Y - (int) config.distanceCalageArriere());
                runAsync(() -> bras.setBrasArriere(PositionBras.INIT));
                mv.gotoOrientationDeg(180);
            }

            mv.setVitessePercent(50, 100);

            enZone = true;

            int targetX = getX(725);
            if (rs.strategy() == Strategy.SUD) {
                targetX = getX(1725);
            }

            servos.groupePanneauOuvert(true);
            mv.gotoPoint(targetX, mv.currentYMm(), GotoOption.SANS_ORIENTATION);
            if (rs.team() == Team.BLEU) {
                io.tournePanneauBleu(VITESSE_ROUE_PANNEAU);
            } else {
                io.tournePanneauJaune(VITESSE_ROUE_PANNEAU);
            }
            ThreadUtils.sleep(500);

        } catch (NoPathFoundException | AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'exécution de l'action : {}", e.toString());

        } finally {
            servosNerell.groupePanneauFerme(false);
            ioService.stopTournePanneau();

            // le nombre de panneaux tournés depend de jusqu'ou on a pu avancer
            if (enZone) {
                int x = getX((int) mv.currentXMm());
                if (x > 1715) {
                    rs.panneauxSolaire().equipeDone(6, rsNerell.getElapsedTime());
                } else if (x > 1490) {
                    rs.panneauxSolaire().equipeDone(5, rsNerell.getElapsedTime());
                } else if (x > 1265) {
                    rs.panneauxSolaire().equipeDone(4, rsNerell.getElapsedTime());
                } else if (x > 715) {
                    rs.panneauxSolaire().equipeDone(3, rsNerell.getElapsedTime());
                } else if (x > 590) {
                    rs.panneauxSolaire().equipeDone(2, rsNerell.getElapsedTime());
                } else if (x > 265) {
                    rs.panneauxSolaire().equipeDone(1, rsNerell.getElapsedTime());
                }

                if (x > 900) {
                    rs.stocksPots().get(rs.team() == Team.JAUNE ? StockPots.ID.JAUNE_SUD : StockPots.ID.BLEU_SUD).bloque();
                }

                rs.panneauxSolaire().triedActionEquipe(true);
                complete(true);
            }
        }
    }
}
