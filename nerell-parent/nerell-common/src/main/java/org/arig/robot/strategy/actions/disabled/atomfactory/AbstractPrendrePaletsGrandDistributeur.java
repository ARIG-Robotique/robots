package org.arig.robot.strategy.actions.disabled.atomfactory;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.exception.RefreshPathFindingException;
import org.arig.robot.exceptions.PinceNotAvailableException;
import org.arig.robot.model.*;
import org.arig.robot.services.PincesService;
import org.arig.robot.services.ServosService;
import org.arig.robot.strategy.AbstractAction;
import org.arig.robot.system.ITrajectoryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public abstract class AbstractPrendrePaletsGrandDistributeur extends AbstractAction {

    final Point posViolet;

    final Point posJaune;

    final int index1;

    final int index2;

    abstract Map<Integer, Palet.Couleur> liste();

    @Autowired
    private ITrajectoryManager mv;

    @Autowired
    protected RobotStatus rs;

    @Autowired
    private ServosService servos;

    @Autowired
    private PincesService pincesService;

    @Getter
    private boolean completed = false;

    @Override
    public boolean isValid() {
        return rs.getCarousel().count(null) >= 2;
    }

    @Override
    public void execute() {
        try {
            rs.enableAvoidance();

            if (rs.getTeam() == Team.VIOLET) {
                mv.pathTo(posViolet.getX(), posViolet.getY());
            } else {
                mv.pathTo(posJaune.getX(), posJaune.getY());
            }

            rs.disableAvoidance();

            // aligne puis avance en position
            mv.gotoOrientationDeg(-90);

            pincesService.waitAvailable(ESide.GAUCHE);
            pincesService.waitAvailable(ESide.DROITE);

            // TODO process optimisé
            // - mettre les deux ventouses en position
            // - avancer
            // - prendre à gauche puis à droite
            // - reculer

            // prise du 1
            if (!pincesService.stockageDistributeur(liste().get(index1), rs.getTeam() == Team.VIOLET ? ESide.GAUCHE : ESide.DROITE)) {
                completed = true;
                return;
            } else {
                liste().put(index1, null);
            }

            // prise du 2
            if (!pincesService.stockageDistributeur(liste().get(index2), rs.getTeam() == Team.VIOLET ? ESide.DROITE : ESide.GAUCHE)) {
                completed = true;
                return;
            } else {
                liste().put(index2, null);
            }

            completed = true;

        } catch (NoPathFoundException | AvoidingException | RefreshPathFindingException | PinceNotAvailableException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
            updateValidTime();
        }
    }
}
