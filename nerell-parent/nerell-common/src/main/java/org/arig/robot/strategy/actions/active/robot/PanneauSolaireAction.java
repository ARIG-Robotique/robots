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
import org.springframework.stereotype.Component;

import static org.arig.robot.constants.NerellConstantesConfig.VITESSE_ROUE_PANNEAU;

@Slf4j
@Component
public class PanneauSolaireAction extends AbstractNerellAction {

    private final int Y_ENTRY = 230;
    private final int Y_ACTION = 230;

    PanneauSolaire firstPanneau;

    // Nombre de tentative de récupération des carrés de fouille
    protected int nbTry = 0;

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
        boolean valid = isTimeValid()
                && rs.panneauxSolaire().triedActionEquipe();
        if (!valid) {
            return false;
        }
        firstPanneau = rs.panneauxSolaire().nextPanneauSolaireToProcess(nbTry, false);
        return firstPanneau != null;
    }

    @Override
    public int order() {
        return rs.panneauxSolairePointRestant();
    }

    @Override
    public Point entryPoint() {
        return new Point(firstPanneau.getX(), Y_ENTRY);
    }

    @Override
    public void execute() {
        boolean stockPotEnVrac = false;
        try {
            final Point entry = entryPoint();
            PanneauSolaire panneau = firstPanneau;
            boolean first = true;
            Double yActionReal = null;

            do {
                panneau.incrementTry();
                log.info("Goto panneau solaire {} / Try {}", panneau.numero(), panneau.nbTry());

                if (first) {
                    mv.setVitessePercent(100, 100);
                    mv.pathTo(entry);
                    yActionReal = callageY();

                    if (yActionReal == null) {
                        panneau = rs.panneauxSolaire().nextPanneauSolaireToProcess(nbTry, false);
                        if (panneau == null) {
                            break;
                        }
                        continue;
                    }

                    mv.gotoOrientationDeg(-180);
                } else {
                    mv.gotoPoint(panneau.getX(), yActionReal);
                }

                if (rs.team() == Team.BLEU) {
                    ioService.tournePanneauBleu(VITESSE_ROUE_PANNEAU);
                } else {
                    ioService.tournePanneauJaune(VITESSE_ROUE_PANNEAU);
                }

                // FIXME position du ski en fonction de la position initiale théorique du panneau

                servosNerell.groupePanneauOuvert(true);

                ThreadUtils.sleep(500);
                panneau.couleur(rs.team() == Team.JAUNE ? CouleurPanneauSolaire.JAUNE : CouleurPanneauSolaire.BLEU);

                servosNerell.groupePanneauFerme(false);

                first = false;

                PanneauSolaire nextPanneau = rs.panneauxSolaire().nextPanneauSolaireToProcess(nbTry, false);

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
            nbTry++;

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
        mv.reculeMM(Y_ENTRY - config.distanceCalageArriere() - 10);

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

        runAsync(() -> bras.brasAvantInit());

        return yActionReal;
    }
}
