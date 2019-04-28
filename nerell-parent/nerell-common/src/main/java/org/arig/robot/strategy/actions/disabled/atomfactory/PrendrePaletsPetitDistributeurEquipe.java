package org.arig.robot.strategy.actions.disabled.atomfactory;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.exception.RefreshPathFindingException;
import org.arig.robot.exceptions.PinceNotAvailableException;
import org.arig.robot.model.ESide;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.Team;
import org.arig.robot.model.enums.CouleurPalet;
import org.arig.robot.services.PincesService;
import org.arig.robot.strategy.AbstractAction;
import org.arig.robot.system.ICarouselManager;
import org.arig.robot.system.ITrajectoryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PrendrePaletsPetitDistributeurEquipe extends AbstractAction {

    @Autowired
    private ITrajectoryManager mv;

    @Autowired
    private RobotStatus rs;

    @Autowired
    private PincesService pinces;

    @Autowired
    private ICarouselManager carousel;

    @Getter
    private boolean completed = false;

    @Override
    public String name() {
        return "Prise des palets dans le petit distributeur de l'équipe";
    }

    @Override
    public int order() {
        return 0; // TODO
    }

    @Override
    public boolean isValid() {
        return isTimeValid() &&
                carousel.count(null) >= 3;
    }

    @Override
    public void execute() {
        ESide sideBleu = rs.getTeam() == Team.VIOLET ? ESide.GAUCHE : ESide.DROITE;
        ESide sideVert = rs.getTeam() == Team.VIOLET ? ESide.DROITE : ESide.GAUCHE;
        ESide sideRouge = rs.getTeam() == Team.VIOLET ? ESide.DROITE : ESide.GAUCHE;

        try {
            rs.enableAvoidance();

            // va au point le plus proche puis au point en face de BLEU/VERT
            // TODO
            if (rs.getTeam() == Team.VIOLET) {
                mv.pathTo(2800, 200);
                rs.disableAvoidance();
                mv.gotoPointMM(2850, 150);
            } else {
                mv.pathTo(200, 200);
                rs.disableAvoidance();
                mv.gotoPointMM(150, 150);
            }

            // aligne, prépare les pinces et avance
            mv.gotoOrientationDeg(-90);

            pinces.waitAvailable(ESide.GAUCHE);
            pinces.waitAvailable(ESide.DROITE);

            pinces.preparePriseDistributeur(ESide.GAUCHE);
            pinces.preparePriseDistributeur(ESide.DROITE);

            mv.avanceMM(150); // TODO

            // prise du bleu et du vert
            boolean bleuOk = pinces.priseDistributeur(CouleurPalet.BLEU, sideBleu);
            boolean vertOk = pinces.priseDistributeur(CouleurPalet.VERT, sideVert);

            // recule
            mv.reculeMM(150); // TODO

            // stocke
            pinces.finishPriseDistributeur(bleuOk, sideBleu);
            pinces.finishPriseDistributeur(vertOk, sideVert);

            pinces.stockageAsync(sideBleu);
            pinces.stockageAsync(sideVert);

            // avance en face du ROUGE
            // TODO
            if (rs.getTeam() == Team.VIOLET) {
                mv.gotoPointMM(2750, 150);
            } else {
                mv.gotoPointMM(250, 150);
            }

            // aligne, prépare les pinces et avance
            mv.gotoOrientationDeg(-90);

            mv.avanceMM(150); // TODO

            pinces.waitAvailable(sideRouge);
            pinces.preparePriseDistributeur(sideRouge);

            // prise du rouge
            boolean rougeOk = pinces.priseDistributeur(CouleurPalet.ROUGE, sideRouge);

            // recule
            mv.reculeMM(150); // TODO

            // stocke
            pinces.finishPriseDistributeur(rougeOk, sideRouge);
            pinces.stockageAsync(sideRouge);

            // TODO
            rs.enableAvoidance();
            if (rs.getTeam() == Team.VIOLET) {
                mv.pathTo(2800, 200);
            } else {
                mv.pathTo(200, 200);
            }

            if (bleuOk) {
                rs.getPaletsPetitDistributeur().put(0, null);
            }
            if (vertOk) {
                rs.getPaletsPetitDistributeur().put(1, null);
            }
            if (rougeOk) {
                rs.getPaletsPetitDistributeur().put(2, null);
            }

            completed = true;

        } catch (NoPathFoundException | AvoidingException | RefreshPathFindingException | PinceNotAvailableException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
            updateValidTime();

        } finally {
            pinces.finishPriseDistributeur(false, ESide.GAUCHE);
            pinces.finishPriseDistributeur(false, ESide.DROITE);
        }
    }
}
