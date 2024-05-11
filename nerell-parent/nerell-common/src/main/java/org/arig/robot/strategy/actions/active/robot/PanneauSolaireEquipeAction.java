package org.arig.robot.strategy.actions.active.robot;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.CouleurPanneauSolaire;
import org.arig.robot.model.PanneauSolaire;
import org.arig.robot.model.Point;
import org.arig.robot.model.StockPots;
import org.arig.robot.model.Strategy;
import org.arig.robot.model.Team;
import org.arig.robot.model.bras.PositionBras;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.model.enums.TypeCalage;
import org.arig.robot.strategy.actions.AbstractNerellAction;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.stereotype.Component;

import static org.arig.robot.constants.NerellConstantesConfig.VITESSE_ROUE_PANNEAU;

@Slf4j
@Component
public class PanneauSolaireEquipeAction extends AbstractNerellAction {
    public static final int ENTRY_X = 210;
    public static final int ENTRY_Y = 210;

    public static final int WORK_Y = 230;

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
        if (rs.strategy() == Strategy.NORD && rs.getRemainingTime() > EurobotConfig.matchTimeMs / 2) {
            return false;
        }

        return isTimeValid()
                && rs.bras().arriereLibre()
                && !rs.panneauxSolaire().equipeDone();
    }

    @Override
    public int order() {
        if (rs.strategy() == Strategy.SUD) {
            return 1000;
        }
        if (!rs.panneauxSolaire().communModifiedByOpponent()) {
            return 30 + tableUtils.alterOrder(entryPoint());
        }
        return 15 + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public Point entryPoint() {
        return new Point(getX(ENTRY_X), ENTRY_Y);
    }

    @Override
    public void execute() {

        boolean enZone = false;
        int yCallage;

        try {
            if (rs.strategy() != Strategy.SUD) {
                final Point entry = entryPoint();

                mv.setVitessePercent(100, 100);
                mv.pathTo(entry);

                // callage Y
                mv.setVitessePercent(60, 100);
                mv.gotoOrientationDeg(90);
                bras.setBrasArriere(PositionBras.CALLAGE_PANNEAUX);
                rs.enableCalageBordure(TypeCalage.ARRIERE, TypeCalage.FORCE);
                mv.reculeMM((int) mv.currentYMm() - config.distanceCalageArriere() - 10);

                if (rs.calageCompleted().contains(TypeCalage.FORCE)) {
                    log.warn("Blocage pendant le callage du panneau");
                    mv.avanceMM(100);
                    runAsync(() -> bras.setBrasArriere(PositionBras.INIT));
                    rs.panneauxSolaire().get(rs.team() == Team.BLEU ? 1 : 9).blocked(true);
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
                yCallage = (int) mv.currentYMm();
                runAsync(() -> bras.setBrasArriere(PositionBras.INIT));
                mv.gotoOrientationDeg(180);
            } else {
                rs.enableAvoidance();
                yCallage = (int) mv.currentYMm();
            }

            mv.setVitessePercent(50, 100);

            enZone = true;

            int targetX = getX(725);
            boolean doCommun = true;
//            if (rs.strategy() == Strategy.SUD || !rs.panneauxSolaire().communModifiedByOpponent()) {
//                targetX = getX(1725);
//                doCommun = false;
//            }

            servos.groupePanneauOuvert(true);
            mv.gotoPoint(targetX, yCallage, GotoOption.SANS_ORIENTATION);
            if (rs.team() == Team.BLEU) {
                io.tournePanneauBleu(VITESSE_ROUE_PANNEAU);
            } else {
                io.tournePanneauJaune(VITESSE_ROUE_PANNEAU);
            }
            ThreadUtils.sleep(300);

            if (doCommun) {
                enZone = false;
                servosNerell.groupePanneauFerme(false);
                ioService.stopTournePanneau();

                rs.panneauxSolaire().equipeDone(3, rs.getElapsedTime());

                int firstId = rs.team() == Team.BLEU ? 4 : 6;
                int lastId = rs.team() == Team.BLEU ? 6 : 4;
                int inc = rs.team() == Team.BLEU ? 1 : -1;
                for (int id = firstId; rs.team() == Team.BLEU ? id <= lastId : id >= lastId; id += inc) {
                    PanneauSolaire panneau = rs.panneauxSolaire().get(id);

                    log.info("Goto panneau solaire {}", panneau.numero());

                    mv.gotoPoint(panneau.getX(), yCallage);

                    if (rs.team() == Team.BLEU) {
                        ioService.tournePanneauBleu(1024);
                    } else {
                        ioService.tournePanneauJaune(1024);
                    }

                    if (panneau.rotation() != null && Math.abs(panneau.rotation()) >= 125) {
                        servosNerell.setPanneauSolaireRoueOuvert(true);
                        ThreadUtils.sleep(50);
                        servosNerell.setPanneauSolaireSkiOuvert(true);
                        ThreadUtils.sleep(400);
                    } else {
                        servosNerell.groupePanneauOuvert(true);
                        ThreadUtils.sleep(500);
                    }

                    panneau.couleur(rs.team() == Team.JAUNE ? CouleurPanneauSolaire.JAUNE : CouleurPanneauSolaire.BLEU)
                            .millis(rsNerell.getElapsedTime())
                            .rotation(null);

                    servosNerell.groupePanneauFerme(false);

                    if (rs.getRemainingTime() < 10000) {
                        break;
                    }
                }
                ;
            }

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

                complete(true);
            }
        }
    }
}
