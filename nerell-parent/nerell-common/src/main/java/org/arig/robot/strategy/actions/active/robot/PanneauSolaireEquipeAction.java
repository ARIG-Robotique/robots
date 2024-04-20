package org.arig.robot.strategy.actions.active.robot;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Point;
import org.arig.robot.model.Team;
import org.arig.robot.model.bras.PositionBras;
import org.arig.robot.model.enums.TypeCalage;
import org.arig.robot.services.BrasService;
import org.arig.robot.strategy.actions.AbstractNerellAction;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.arig.robot.constants.NerellConstantesConfig.VITESSE_ROUE_PANNEAU;

@Slf4j
@Component
public class PanneauSolaireEquipeAction extends AbstractNerellAction {
    final int ENTRY_X = 210;
    final int ENTRY_Y = 210;

    final int WORK_Y = 230;

    @Autowired
    private BrasService bras;

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
        return rs.bras().arriereLibre() && !rs.panneauxSolaire().equipeDone();
    }

    @Override
    public int order() {
        return 15;
    }

    @Override
    public Point entryPoint() {
        return new Point(getX(ENTRY_X), ENTRY_Y);
    }

    @Override
    public void execute() {

        boolean enZone = false;

        try {
            final Point entry = entryPoint();

            mv.setVitesse(config.vitesse(), config.vitesseOrientation());
            mv.pathTo(entry);

            // callage Y
            mv.setVitesse(config.vitesse(60), config.vitesseOrientation());
            mv.gotoOrientationDeg(90);
            bras.setBrasArriere(PositionBras.CALLAGE_PANNEAUX);
            rs.enableCalageBordure(TypeCalage.ARRIERE);
            mv.reculeMM(ENTRY_Y - config.distanceCalageArriere() - 10);

            mv.setVitesse(config.vitesse(0), config.vitesseOrientation());
            rs.enableCalageBordure(TypeCalage.ARRIERE);
            mv.reculeMMSansAngle(40);
            checkRecalageYmm(config.distanceCalageArriere(), TypeCalage.ARRIERE);
            checkRecalageAngleDeg(90, TypeCalage.ARRIERE);

            // les déplacements sont relatifs pour rester à la bonne distance de la bordure
            mv.setVitesse(config.vitesse(60), config.vitesseOrientation());
            mv.avanceMM(WORK_Y - (int) config.distanceCalageArriere());
            bras.setBrasArriere(PositionBras.INIT);
            mv.gotoOrientationDeg(180);

            // callage X
            if (rs.team() == Team.BLEU) {
                rs.enableCalageBordure(TypeCalage.AVANT);
                mv.avanceMM(ENTRY_X - config.distanceCalageAvant() - 10);

                mv.setVitesse(config.vitesse(0), config.vitesseOrientation());
                rs.enableCalageBordure(TypeCalage.AVANT);
                mv.avanceMMSansAngle(40);
                checkRecalageXmm(config.distanceCalageAvant(), TypeCalage.AVANT);

                mv.setVitesse(config.vitesse(35), config.vitesseOrientation());
                mv.reculeMM(ENTRY_X - config.distanceCalageAvant());
            } else {
                rs.enableCalageBordure(TypeCalage.ARRIERE);
                mv.reculeMM(ENTRY_X - config.distanceCalageAvant() - 10);

                mv.setVitesse(config.vitesse(0), config.vitesseOrientation());
                rs.enableCalageBordure(TypeCalage.ARRIERE);
                mv.reculeMMSansAngle(40);
                checkRecalageXmm(getX((int) config.distanceCalageArriere()), TypeCalage.ARRIERE);

                mv.setVitesse(config.vitesse(35), config.vitesseOrientation());
                mv.avanceMM(ENTRY_X - config.distanceCalageArriere());
            }

            enZone = true;

            servos.groupePanneauOuvert(false);

            if (rs.team() == Team.BLEU) {
                io.tournePanneauBleu(VITESSE_ROUE_PANNEAU);
                mv.reculeMM(275 - ENTRY_X + 225 * 2 + 40);
            } else {
                io.tournePanneauJaune(VITESSE_ROUE_PANNEAU);
                mv.avanceMM(275 - ENTRY_X + 225 * 2 + 40);
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
                if (x > 725) {
                    rs.panneauxSolaire().equipeDone(3);
                } else if (x > 500) {
                    rs.panneauxSolaire().equipeDone(2);
                } else if (x > 275) {
                    rs.panneauxSolaire().equipeDone(1);
                }
            }
        }
    }
}
