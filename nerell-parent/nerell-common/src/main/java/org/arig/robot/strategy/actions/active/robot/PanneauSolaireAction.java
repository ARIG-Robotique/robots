package org.arig.robot.strategy.actions.active.robot;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.CouleurPanneauSolaire;
import org.arig.robot.model.PanneauSolaire;
import org.arig.robot.model.Point;
import org.arig.robot.model.StockPots;
import org.arig.robot.model.Team;
import org.arig.robot.model.bras.PositionBras;
import org.arig.robot.model.enums.TypeCalage;
import org.arig.robot.strategy.actions.AbstractNerellAction;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PanneauSolaireAction extends AbstractNerellAction {

    @Autowired(required = false)
    PanneauSolaireEquipeAction panneauSolaireEquipeAction;

    private final int Y_ENTRY = 235;
    private final int Y_ACTION = 235;

    @Override
    public String name() {
        return EurobotConfig.ACTION_PANNEAU_SOLAIRE_COMMUN;
    }

    @Override
    public int executionTimeMs() {
        return 0;
    }

    @Override
    public boolean isValid() {
        if (!isTimeValid() || ilEstTempsDeRentrer() || !rs.bras().arriereLibre()) {
            return false;
        }

        // prio à l'action qui fait tout d'un coup
        if (panneauSolaireEquipeAction != null && !panneauSolaireEquipeAction.isCompleted()) {
            return false;
        }

        // vraiment quelque chose à faire
        PanneauSolaire firstPanneau = firstPanneau();
        if (firstPanneau == null || entryPanneau(firstPanneau) == null) {
            return false;
        }

        // prio en fin de match on si l'adversaire est déjà venu les faire
        return rs.getRemainingTime() < 30000 || rs.panneauxSolaire().communModifiedByOpponent();
    }

    @Override
    public int order() {
        Point entryPoint = entryPoint();
        if (entryPoint == null) {
            return 0;
        }
        return rs.panneauxSolairePointRestant() + tableUtils.alterOrder(entryPoint);
    }

    @Override
    public Point entryPoint() {
        PanneauSolaire entryPanneau = entryPanneau(firstPanneau());
        if (entryPanneau == null) {
            return null;
        }
        return new Point(entryPanneau.getX(), Y_ENTRY);
    }

    private boolean isReverse() {
        return getX((int) mv.currentXMm()) > 1500;
    }

    private PanneauSolaire firstPanneau() {
        return rs.panneauxSolaire().nextPanneauSolaireToProcess(isReverse(), rs.mines());
    }

    private PanneauSolaire entryPanneau(PanneauSolaire firstPanneau) {
        if (firstPanneau == null) {
            return null;
        }
        return rs.panneauxSolaire().entryPanneau(firstPanneau, rs.mines());
    }

    @Override
    public void execute() {
        boolean stockPotEnVrac = false;
        try {
            final Point entry = entryPoint();
            PanneauSolaire panneau = firstPanneau();
            PanneauSolaire entryPanneau = entryPanneau(panneau);
            boolean first = true;
            Double yActionReal = null;

            do {
                log.info("Goto panneau solaire {}", panneau.numero());

                if (first) {
                    mv.setVitessePercent(100, 100);
                    mv.pathTo(entry);
                    yActionReal = callageY();

                    if (yActionReal == null) {
                        entryPanneau.blocked(true);
                        log.info("[rs] panneau {} blocked", entryPanneau.numero());
                        break;
                    }

                    mv.gotoOrientationDeg(-180);

                    if (panneau.blocked()) {
                        mv.gotoPoint(panneau.getX(), yActionReal);
                    }
                } else {
                    mv.gotoPoint(panneau.getX(), yActionReal);
                }

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

                first = false;

                if (rs.getRemainingTime() < 10000) {
                    break;
                }
                PanneauSolaire nextPanneau = firstPanneau();

                if (nextPanneau != null) {
                    // si on change de groupe on refait un path + callage
                    if (((panneau.numero() - 1) / 3) != ((nextPanneau.numero() - 1) / 3)) {
                        first = true;
                    }
                }

                panneau = nextPanneau;
            } while (panneau != null);

        } catch (NoPathFoundException | AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'exécution de l'action : {}", e.toString());
        } finally {
            servosNerell.groupePanneauFerme(false);
            ioService.stopTournePanneau();

            if (stockPotEnVrac) {
                if (rs.team() == Team.BLEU) {
                    rs.stocksPots().get(StockPots.ID.BLEU_SUD).bloque();
                } else {
                    rs.stocksPots().get(StockPots.ID.JAUNE_SUD).bloque();
                }
            }
        }
    }

    private Double callageY() throws AvoidingException {
        // callage Y
        mv.setVitessePercent(60, 100);
        mv.gotoOrientationDeg(90);
        bras.setBrasArriere(PositionBras.CALLAGE_PANNEAUX);
        rs.enableCalageBordure(TypeCalage.ARRIERE, TypeCalage.FORCE);
        mv.reculeMM((int) mv.currentYMm() - config.distanceCalageArriere() - 10);

        if (rs.calageCompleted().contains(TypeCalage.FORCE)) {
            log.warn("Blocage pendant le callage du panneau");
            runAsync(() -> bras.setBrasArriere(PositionBras.INIT));
            mv.avanceMM(100);
            return null;
        }

        mv.setVitessePercent(0, 100);
        rs.enableCalageBordure(TypeCalage.ARRIERE);
        mv.reculeMMSansAngle(60);
        checkRecalageYmm(config.distanceCalageArriere(), TypeCalage.ARRIERE);
        checkRecalageAngleDeg(90, TypeCalage.ARRIERE);

        mv.setVitessePercent(100, 100);
        mv.avanceMM(Y_ACTION - config.distanceCalageArriere());
        double yActionReal = mv.currentYMm();

        runAsync(() -> bras.setBrasArriere(PositionBras.INIT));

        return yActionReal;
    }
}
