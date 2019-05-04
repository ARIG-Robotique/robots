package org.arig.robot.strategy.actions.disabled.atomfactory;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.exception.RefreshPathFindingException;
import org.arig.robot.exceptions.VentouseNotAvailableException;
import org.arig.robot.model.ESide;
import org.arig.robot.model.Position;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.Team;
import org.arig.robot.model.enums.CouleurPalet;
import org.arig.robot.services.VentousesService;
import org.arig.robot.strategy.AbstractAction;
import org.arig.robot.system.ICarouselManager;
import org.arig.robot.system.ITrajectoryManager;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PrendrePaletsPetitDistributeurEquipe extends AbstractAction {

    @Autowired
    private ITrajectoryManager mv;

    @Autowired
    private RobotStatus rs;

    @Autowired
    private VentousesService ventouses;

    @Autowired
    private ICarouselManager carousel;

    @Autowired
    @Qualifier("currentPosition")
    private Position position;

    @Autowired
    private ConvertionRobotUnit conv;

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
            if (rs.getTeam() == Team.VIOLET) {
                mv.pathTo(3000 - 235, 235);
                rs.disableAvoidance();
                mv.gotoPointMM(3000 - 175, 200);
            } else {
                mv.pathTo(235, 235);
                rs.disableAvoidance();
                mv.gotoPointMM(175, 200);
            }

            // aligne, prépare les ventouses et avance
            mv.gotoOrientationDeg(-90);

            ventouses.waitAvailable(ESide.GAUCHE);
            ventouses.waitAvailable(ESide.DROITE);

            ventouses.preparePriseDistributeur(ESide.GAUCHE);
            ventouses.preparePriseDistributeur(ESide.DROITE);

            mv.gotoPointMM(conv.pulseToMm(position.getPt().getX()), IConstantesNerellConfig.dstVentouseFacade);

            // prise du bleu et du vert
            boolean bleuOk = ventouses.priseDistributeur(CouleurPalet.BLEU, sideBleu);
            boolean vertOk = ventouses.priseDistributeur(CouleurPalet.VERT, sideVert);

            // recule
            mv.reculeMM(200);

            // stocke
            ventouses.finishPriseDistributeurAsync(bleuOk, sideBleu);
            ventouses.finishPriseDistributeurAsync(vertOk, sideVert);

            // avance en face du ROUGE
            if (rs.getTeam() == Team.VIOLET) {
                mv.gotoPointMM(3000 - 275, 150);
            } else {
                mv.gotoPointMM(275, 200);
            }

            // aligne, prépare les ventouses et avance
            mv.gotoOrientationDeg(-90);

            mv.gotoPointMM(conv.pulseToMm(position.getPt().getX()), IConstantesNerellConfig.dstVentouseFacade);

            ventouses.waitAvailable(sideRouge);
            ventouses.preparePriseDistributeur(sideRouge);

            // prise du rouge
            boolean rougeOk = ventouses.priseDistributeur(CouleurPalet.ROUGE, sideRouge);

            // recule
            mv.reculeMM(50);

            // stocke
            ventouses.finishPriseDistributeurAsync(rougeOk, sideRouge);

            rs.enableAvoidance();
            if (rs.getTeam() == Team.VIOLET) {
                mv.gotoPointMM(3000 - 245, 600);
            } else {
                mv.gotoPointMM(245, 600);
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

        } catch (NoPathFoundException | AvoidingException | RefreshPathFindingException | VentouseNotAvailableException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
            updateValidTime();
        }
    }
}
