package org.arig.robot.strategy.actions.disabled.atomfactory;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.exception.RefreshPathFindingException;
import org.arig.robot.exceptions.VentouseNotAvailableException;
import org.arig.robot.model.ESide;
import org.arig.robot.model.Point;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.Team;
import org.arig.robot.model.enums.CouleurPalet;
import org.arig.robot.services.VentousesService;
import org.arig.robot.strategy.AbstractAction;
import org.arig.robot.system.ICarouselManager;
import org.arig.robot.system.ITrajectoryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public abstract class AbstractPrendrePaletsGrandDistributeur extends AbstractAction {

    final Point posViolet;

    final Point posJaune;

    final int index1;

    final int index2;

    abstract Map<Integer, CouleurPalet> liste();

    @Autowired
    private ITrajectoryManager mv;

    @Autowired
    protected RobotStatus rs;

    @Autowired
    private VentousesService ventouses;

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
    public void execute() {
        ESide side1 = rs.getTeam() == Team.VIOLET ? ESide.GAUCHE : ESide.DROITE;
        ESide side2 = rs.getTeam() == Team.VIOLET ? ESide.DROITE : ESide.GAUCHE;

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

            ventouses.waitAvailable(ESide.GAUCHE);
            ventouses.waitAvailable(ESide.DROITE);

            ventouses.preparePriseDistributeur(ESide.GAUCHE);
            ventouses.preparePriseDistributeur(ESide.DROITE);

            mv.avanceMM(150); // TODO

            // prise du 1 et du 2
            boolean ok1 = ventouses.priseDistributeur(liste().get(index1), side1);
            boolean ok2 = ventouses.priseDistributeur(liste().get(index2), side2);

            // recule
            mv.reculeMM(150); // TODO

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
