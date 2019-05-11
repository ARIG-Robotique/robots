package org.arig.robot.strategy.actions.active;

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
import org.arig.robot.services.VentousesService;
import org.arig.robot.strategy.AbstractAction;
import org.arig.robot.system.ITrajectoryManager;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public abstract class AbstractPrendreGrandDistributeur extends AbstractAction {

    final int xViolet;

    final int xJaune;

    final int index1;

    final int index2;

    final int orderMux;

    public abstract Map<Integer, CouleurPalet> liste();

    @Autowired
    private ITrajectoryManager mv;

    @Autowired
    protected RobotStatus rs;

    @Autowired
    private VentousesService ventouses;

    @Getter
    private boolean completed = false;

    @Override
    public boolean isValid() {
        return isTimeValid() &&
                ventouses.getCouleur(ESide.GAUCHE) == null &&
                ventouses.getCouleur(ESide.DROITE) == null;
    }

    @Override
    public int order() {
        return orderMux * (liste().get(index1).getImportance() + liste().get(index2).getImportance());
    }

    @Override
    public void execute() {
        ESide side1 = rs.getTeam() == Team.VIOLET ? ESide.GAUCHE : ESide.DROITE;
        ESide side2 = rs.getTeam() == Team.VIOLET ? ESide.DROITE : ESide.GAUCHE;

        mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);

        try {
            rs.enableAvoidance();

            int yAvantAvance = 680;

            if (rs.getTeam() == Team.VIOLET) {
                mv.pathTo(xViolet, 680);
            } else {
                mv.pathTo(xJaune, 680);
            }

            rs.disableAvoidance();

            // aligne puis avance en position
            mv.gotoOrientationDeg(-90);

            ventouses.waitAvailable(ESide.GAUCHE);
            ventouses.waitAvailable(ESide.DROITE);

            ventouses.preparePriseDistributeur(ESide.GAUCHE);
            ventouses.preparePriseDistributeur(ESide.DROITE);

            // 457 = distance bord distributeur
            double yOffset = -457 + yAvantAvance - IConstantesNerellConfig.dstVentouseFacade;

            mv.avanceMM(yOffset);

            ThreadUtils.sleep(500);

            // prise du 1 et du 2
            boolean ok1 = ventouses.priseDistributeur(liste().get(index1), side1);
            boolean ok2 = ventouses.priseDistributeur(liste().get(index2), side2);

            // recule
            mv.reculeMM(50);

            // stocke
            ventouses.finishPriseDistributeurAsync(ok1, side1);
            ventouses.finishPriseDistributeurAsync(ok2, side2);

            completed = true;

        } catch (NoPathFoundException | AvoidingException | RefreshPathFindingException | VentouseNotAvailableException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
            updateValidTime();
        }
    }
}
