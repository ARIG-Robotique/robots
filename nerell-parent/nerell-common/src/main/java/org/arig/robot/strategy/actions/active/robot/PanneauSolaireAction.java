package org.arig.robot.strategy.actions.active.robot;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.CouleurPanneauSolaire;
import org.arig.robot.model.PanneauSolaire;
import org.arig.robot.model.Point;
import org.arig.robot.model.Team;
import org.arig.robot.strategy.actions.AbstractNerellAction;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PanneauSolaireAction extends AbstractNerellAction {

    private final int Y_ENTRY = 230;
    private final int Y_ACTION = 230;

    PanneauSolaire firstPanneau;

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
        firstPanneau = rs.nextPanneauSolaire(10, false);
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
        try {
            final Point entry = entryPoint();
            PanneauSolaire panneau = firstPanneau;
            boolean first = true;

            do {
                panneau.incrementTry();
                log.info("Goto panneau solaire {} / Try {}", panneau.numero(), panneau.nbTry());

                if (first) {
                    mv.setVitesse(config.vitesse(), config.vitesseOrientation());
                    mv.pathTo(entry);
                }

                mv.gotoPoint(panneau.getX(), Y_ACTION);
                mv.gotoOrientationDeg(-180);

                // FIXME position du ski en fonction de la position initiale théorique du panneau

                servosNerell.groupePanneauOuvert(true);

                if (rs.team() == Team.BLEU) {
                    ioService.tournePanneauAvant();
                } else {
                    ioService.tournePanneauArriere();
                }
                ThreadUtils.sleep(1000);
                panneau.couleur(rs.team() == Team.JAUNE ? CouleurPanneauSolaire.JAUNE : CouleurPanneauSolaire.BLEU);

                servosNerell.groupePanneauFerme(false);

                first = false;
                panneau = rs.nextPanneauSolaire(10, false);

            } while (panneau != null);

            log.info("Sortie de la zone des panneauxé");
            mv.gotoPoint(mv.currentXMm(), Y_ENTRY);

        } catch (NoPathFoundException | AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'exécution de l'action : {}", e.toString());
        } finally {
            servosNerell.groupePanneauFerme(false);
            ioService.stopTournePanneau();
        }
    }
}
