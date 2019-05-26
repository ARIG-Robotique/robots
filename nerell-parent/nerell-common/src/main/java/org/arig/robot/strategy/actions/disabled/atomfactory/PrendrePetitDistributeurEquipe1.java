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
import org.arig.robot.services.VentousesService;
import org.arig.robot.strategy.AbstractAction;
import org.arig.robot.system.ICarouselManager;
import org.arig.robot.system.ITrajectoryManager;
import org.arig.robot.utils.NerellUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Slf4j
@Component
public class PrendrePetitDistributeurEquipe1 extends AbstractAction {

    @Autowired
    private ITrajectoryManager mv;

    @Autowired
    private RobotStatus rs;

    @Autowired
    private VentousesService ventouses;

    @Autowired
    private ICarouselManager carousel;

    @Getter
    private boolean completed = false;

    @Override
    public String name() {
        return "Prise des palets dans le petit distributeur 1 de l'équipe";
    }

    @Override
    public int order() {
        return 2 * (CouleurPalet.BLEU.getImportance() + CouleurPalet.VERT.getImportance());
    }

    @Override
    public boolean isValid() {
        return isTimeValid() &&
                carousel.count(null) >= 2;
    }

    @Override
    public void execute() {
        ESide sideBleu = rs.getTeam() == Team.VIOLET ? ESide.GAUCHE : ESide.DROITE;
        ESide sideVert = rs.getTeam() == Team.VIOLET ? ESide.DROITE : ESide.GAUCHE;

        try {

            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);

            rs.enableAvoidance();

            int yAvantAvance = 200;
            // 75 = position bord distrib, 50 = position centre palet/bord distrib
            double xPos = 75 + 50 + IConstantesNerellConfig.dstAtomeCentre;

            // va au point le plus proche puis au point en face de BLEU/VERT
            if (rs.getTeam() == Team.VIOLET) {
                mv.pathTo(3000 - 235, 235);
                rs.disableAvoidance();
                mv.gotoPointMM(3000 - xPos, yAvantAvance);
            } else {
                mv.pathTo(235, 235);
                rs.disableAvoidance();
                mv.gotoPointMM(xPos, yAvantAvance);
            }

            // aligne, prépare les ventouses et avance
            mv.gotoOrientationDeg(-90);

            ventouses.waitAvailable(ESide.GAUCHE);
            ventouses.waitAvailable(ESide.DROITE);

            ventouses.preparePriseDistributeur(ESide.GAUCHE);
            ventouses.preparePriseDistributeur(ESide.DROITE);

            mv.avanceMM(yAvantAvance - IConstantesNerellConfig.dstVentouseFacade);

//            rs.enableCalageBordureAvant(IConstantesNerellConfig.dstVentouseFacade);
//            mv.avanceMM(500);

            // prise du bleu et du vert
            NerellUtils.CompoundFutureResult2<Boolean, Boolean> ok = NerellUtils.all(
                    ventouses.priseDistributeur(CouleurPalet.BLEU, sideBleu),
                    ventouses.priseDistributeur(CouleurPalet.VERT, sideVert)
            ).get();
            boolean bleuOk = ok.getA();
            boolean vertOk = ok.getB();

            // recule
            mv.reculeMM(50);

            // stocke
            ventouses.finishPriseDistributeur(bleuOk, sideBleu);
            ventouses.finishPriseDistributeur(vertOk, sideVert);

            if (bleuOk) {
                rs.getPaletsPetitDistributeur().put(0, null);
            }
            if (vertOk) {
                rs.getPaletsPetitDistributeur().put(1, null);
            }

        } catch (NoPathFoundException | AvoidingException | RefreshPathFindingException | VentouseNotAvailableException | InterruptedException | ExecutionException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
            updateValidTime();
        } finally {
            completed = true;
        }
    }
}
