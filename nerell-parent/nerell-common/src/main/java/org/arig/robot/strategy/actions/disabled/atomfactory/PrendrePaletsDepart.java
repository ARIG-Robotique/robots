package org.arig.robot.strategy.actions.disabled.atomfactory;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Triple;
import org.arig.robot.exception.RefreshPathFindingException;
import org.arig.robot.model.*;
import org.arig.robot.services.PincesService;
import org.arig.robot.services.ServosService;
import org.arig.robot.strategy.AbstractAction;
import org.arig.robot.system.ITrajectoryManager;
import org.arig.robot.utils.NerellUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class PrendrePaletsDepart extends AbstractAction {

    @Autowired
    private ITrajectoryManager mv;

    @Autowired
    private RobotStatus rs;

    @Autowired
    private ServosService servos;

    @Autowired
    private PincesService pinces;

    @Autowired
    @Qualifier("currentPosition")
    private Position currentPosition;

    @Getter
    private boolean completed = false;

    @Override
    public String name() {
        return "Prise des palets de départ";
    }

    @Override
    public int order() {
        return Integer.MAX_VALUE - 1;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void execute() {
        try {
            rs.disableAvoidance();

            List<Triple<Point, Palet.Couleur, ESide>> configs = new ArrayList<>();

            if (rs.getTeam().equals(Team.VIOLET)) {
                configs.add(Triple.of(new Point(1550, 2500), Palet.Couleur.ROUGE, ESide.GAUCHE));
                configs.add(Triple.of(new Point(1250, 2500), Palet.Couleur.ROUGE, ESide.DROITE));
                configs.add(Triple.of(new Point(950, 2500), Palet.Couleur.VERT, ESide.GAUCHE));
            } else {
                configs.add(Triple.of(new Point(1550, 500), Palet.Couleur.ROUGE, ESide.DROITE));
                configs.add(Triple.of(new Point(1250, 500), Palet.Couleur.ROUGE, ESide.GAUCHE));
                configs.add(Triple.of(new Point(950, 500), Palet.Couleur.VERT, ESide.DROITE));
            }

            for (Triple<Point, Palet.Couleur, ESide> config : configs) {
                double offsetAngle = NerellUtils.getAngleDecallagePince(currentPosition.getPt(), config.getLeft(), config.getRight());
                double distance = NerellUtils.getDistance(currentPosition.getPt(), config.getLeft());

                mv.alignFrontToAvecDecalage(config.getLeft().getX(), config.getLeft().getY(), offsetAngle);

                pinces.setExpected(config.getRight(), config.getMiddle());

                mv.avanceMM(distance);
            }


        } catch (RefreshPathFindingException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
        } finally {
            // si ça a échoué on a surement shooté dans les palets...
            completed = true;

            pinces.setExpected(ESide.DROITE, null);
            pinces.setExpected(ESide.GAUCHE, null);
        }
    }

}
