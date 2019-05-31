package org.arig.robot.strategy.actions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
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
import org.arig.robot.utils.NerellUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
@RequiredArgsConstructor
public abstract class AbstractPrendreGrandDistributeur extends AbstractAction {

    final int xViolet;

    final int xJaune;

    final int index1;

    final int index2;

    final int orderMux;

    final boolean preferGoto;

    protected abstract Map<Integer, CouleurPalet> liste();

    @Autowired
    private ITrajectoryManager mv;

    @Autowired
    protected RobotStatus rs;

    @Autowired
    private IVentousesService ventouses;

    @Autowired
    private ICarouselManager carousel;

    @Getter
    private boolean completed = false;

    @Override
    public boolean isValid() {
        return isTimeValid() &&
                carousel.count(null) >= 2;
    }

    @Override
    public int order() {
        return orderMux * (liste().get(index1).getImportance() + liste().get(index2).getImportance());
    }

    @Override
    public void execute() {
        execute(true);
    }

    public void execute(boolean async) {
        ESide side1 = rs.getTeam() == Team.VIOLET ? ESide.GAUCHE : ESide.DROITE;
        ESide side2 = rs.getTeam() == Team.VIOLET ? ESide.DROITE : ESide.GAUCHE;

        mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);

        try {
            rs.enableAvoidance();

            int yAvantAvance = 700;

            if (rs.getTeam() == Team.VIOLET) {
                if (preferGoto) {
                    mv.gotoPointMM(xViolet, yAvantAvance, false);
                } else {
                    mv.pathTo(xViolet, yAvantAvance);
                }
            } else {
                if (preferGoto) {
                    mv.gotoPointMM(xJaune, yAvantAvance, false);
                } else {
                    mv.pathTo(xJaune, yAvantAvance);
                }
            }

            rs.disableAvoidance();

            // aligne puis avance en position
            mv.gotoOrientationDeg(-90);

            rs.disableMagasin();

            ventouses.waitAvailable(ESide.GAUCHE);
            ventouses.waitAvailable(ESide.DROITE);

            NerellUtils.all(
                    ventouses.preparePriseDistributeur(ESide.GAUCHE),
                    ventouses.preparePriseDistributeur(ESide.DROITE)
            ).get();

            // 457 = distance bord distributeur, 20 = pour le calage
            double yOffset = -457 + yAvantAvance - IConstantesNerellConfig.dstVentouseFacade + 20;
            rs.enableCalageVentouse();
            mv.avanceMM(yOffset);

            // prise du 1 et du 2
            NerellUtils.CompoundFutureResult2<Boolean, Boolean> ok = NerellUtils.all(
                    ventouses.priseDistributeur(liste().get(index1), side1),
                    ventouses.priseDistributeur(liste().get(index2), side2)
            ).get();
            boolean ok1 = ok.getA();
            boolean ok2 = ok.getB();

            // recule
            mv.reculeMM(50);

            // stocke
            if (async) {
                ventouses.finishPriseDistributeur(ok1, side1);
                ventouses.finishPriseDistributeur(ok2, side2);
            } else {
                NerellUtils.all(
                        ventouses.finishPriseDistributeur(ok1, side1),
                        ventouses.finishPriseDistributeur(ok2, side2)
                ).get();
            }

            completed = true;

        } catch (NoPathFoundException | AvoidingException | RefreshPathFindingException | VentouseNotAvailableException | InterruptedException | ExecutionException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
            updateValidTime();

            ventouses.releaseSide(ESide.DROITE);
            ventouses.releaseSide(ESide.GAUCHE);

            try {
                mv.reculeMM(50);
            } catch (RefreshPathFindingException | AvoidingException ex) {
                ex.printStackTrace();
            }
        }

        //rs.enableMagasin();
    }
}
