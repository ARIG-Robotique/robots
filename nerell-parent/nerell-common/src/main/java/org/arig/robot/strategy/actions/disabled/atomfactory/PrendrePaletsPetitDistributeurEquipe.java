package org.arig.robot.strategy.actions.disabled.atomfactory;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.exception.RefreshPathFindingException;
import org.arig.robot.exceptions.PinceNotAvailableException;
import org.arig.robot.model.ESide;
import org.arig.robot.model.Palet;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.Team;
import org.arig.robot.services.PincesService;
import org.arig.robot.services.ServosService;
import org.arig.robot.strategy.AbstractAction;
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
    private ServosService servos;

    @Autowired
    private PincesService pincesService;

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
        // pas d'autre condition, l'action passe a completed dans tous les cas
        return rs.getCarousel().count(null) >= 3;
    }

    @Override
    public void execute() {
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

            // aligne puis avance en position
            mv.gotoOrientationDeg(-90);

            pincesService.waitAvailable(ESide.GAUCHE);
            pincesService.waitAvailable(ESide.DROITE);

            // TODO process optimisé
            // - mettre les deux ventouses en position
            // - avancer
            // - prendre à gauche puis à droite
            // - reculer

            // prise du bleu
            if (!pincesService.stockageDistributeur(Palet.Couleur.BLEU, rs.getTeam() == Team.VIOLET ? ESide.GAUCHE : ESide.DROITE)) {
                completed = true;
                echappement();
                return;
            } else {
                rs.getPaletsPetitDistributeur().put(0, null);
            }

            // prise du vert
            if (!pincesService.stockageDistributeur(Palet.Couleur.VERT, rs.getTeam() == Team.VIOLET ? ESide.DROITE : ESide.GAUCHE)) {
                completed = true;
                echappement();
                return;
            } else {
                rs.getPaletsPetitDistributeur().put(1, null);
            }

            // recule
            mv.reculeMM(150); // TODO

            // avance en face du ROUGE/VERT
            // TODO
            if (rs.getTeam() == Team.VIOLET) {
                mv.gotoPointMM(2750, 150);
            } else {
                mv.gotoPointMM(250, 150);
            }

            //aligne puis avance en position
            mv.gotoOrientationDeg(-90);

            // prise du rouge
            if (!pincesService.stockageDistributeur(Palet.Couleur.ROUGE, rs.getTeam() == Team.VIOLET ? ESide.DROITE : ESide.GAUCHE)) {
                completed = true;
                echappement();
                return;
            } else {
                rs.getPaletsPetitDistributeur().put(2, null);
            }

            completed = true;
            echappement();

        } catch (NoPathFoundException | AvoidingException | RefreshPathFindingException | PinceNotAvailableException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
            updateValidTime();
        }
    }

    /**
     * Pour se remettre en position pour la suite
     */
    private void echappement() throws RefreshPathFindingException, NoPathFoundException, AvoidingException {
        mv.gotoOrientationDeg(90);

        // TODO
        rs.enableAvoidance();
        if (rs.getTeam() == Team.VIOLET) {
            mv.pathTo(2800, 200);
        } else {
            mv.pathTo(200, 200);
        }
    }
}
