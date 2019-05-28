package org.arig.robot.strategy.actions.disabled.atomfactory;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.exception.RefreshPathFindingException;
import org.arig.robot.exceptions.VentouseNotAvailableException;
import org.arig.robot.model.ESide;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.Team;
import org.arig.robot.model.enums.CouleurPalet;
import org.arig.robot.services.IVentousesService;
import org.arig.robot.strategy.AbstractAction;
import org.arig.robot.system.ICarouselManager;
import org.arig.robot.system.ITrajectoryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Component
@Slf4j
public class PrendrePetitDistributeurEquipe2 extends AbstractAction {

    @Autowired
    private ICarouselManager carousel;

    @Autowired
    private RobotStatus rs;

    @Autowired
    private ITrajectoryManager mv;

    @Autowired
    private IVentousesService ventouses;

    @Getter
    private boolean completed = false;

    @Override
    public String name() {
        return "Prise des palets dans le petit distributeur 1 de l'équipe";
    }

    @Override
    public int order() {
        return CouleurPalet.ROUGE.getImportance() * 2;
    }

    @Override
    public boolean isValid() {
        return isTimeValid() &&
                carousel.count(null) >= 1;
    }

    @Override
    public void execute() {
        ESide sideRouge = rs.getTeam() == Team.VIOLET ? ESide.DROITE : ESide.GAUCHE;

        try {

            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);

            rs.enableAvoidance();

            int yAvantAvance = 200;
            // 75 = position bord distrib, 50 = position centre palet/bord distrib, 100 = distance par rapport le premier palet gauche ou droite
            double xPos = 75 + 50 + IConstantesNerellConfig.dstAtomeCentre + 100;

            // avance en face du ROUGE
            if (rs.getTeam() == Team.VIOLET) {
                mv.pathTo(3000 - 275, 235);
                rs.disableAvoidance();

                mv.gotoPointMM(3000 - xPos, yAvantAvance);
            } else {
                mv.pathTo(275, 235);
                rs.disableAvoidance();

                mv.gotoPointMM(xPos, yAvantAvance);
            }

            // aligne, prépare les ventouses et avance
            mv.gotoOrientationDeg(-90);

            ventouses.waitAvailable(sideRouge);
            ventouses.preparePriseDistributeur(sideRouge);

            rs.enableCalageVentouse();
            mv.avanceMM(yAvantAvance - IConstantesNerellConfig.dstVentouseFacade + 20);

            // prise du rouge
            boolean rougeOk = ventouses.priseDistributeur(CouleurPalet.ROUGE, sideRouge).get();

            if (rougeOk) {
                rs.getPaletsPetitDistributeur().put(3, null);
            }

            // recule
            mv.reculeMM(50);

            // stocke
            ventouses.finishPriseDistributeur(rougeOk, sideRouge);

        } catch (NoPathFoundException | AvoidingException | RefreshPathFindingException | VentouseNotAvailableException | InterruptedException | ExecutionException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
            updateValidTime();
        } finally {
            completed = true;
        }
    }
}
