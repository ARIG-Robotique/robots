package org.arig.robot.strategy.actions.disabled.atomfactory;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.arig.robot.exception.RefreshPathFindingException;
import org.arig.robot.model.ESide;
import org.arig.robot.model.Point;
import org.arig.robot.model.Position;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.Team;
import org.arig.robot.strategy.AbstractAction;
import org.arig.robot.system.ITrajectoryManager;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.arig.robot.utils.NerellUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class PrendreAtomesDepart extends AbstractAction {

    @Autowired
    private ITrajectoryManager mv;

    @Autowired
    private RobotStatus rs;

    @Autowired
    @Qualifier("currentPosition")
    private Position currentPosition;

    @Autowired
    private ConvertionRobotUnit conv;

    @Getter
    private boolean completed = false;

    @Override
    public String name() {
        return "Prise des palets de départ";
    }

    @Override
    public int order() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void execute() {
        try {
            rs.disableAvoidance();

            List<Pair<Point, ESide>> configs = new ArrayList<>();

            if (rs.getTeam().equals(Team.VIOLET)) {
                configs.add(Pair.of(new Point(2500, 1550), ESide.DROITE));
                configs.add(Pair.of(new Point(2500, 1250), ESide.GAUCHE));
                configs.add(Pair.of(new Point(2500, 950), ESide.DROITE));
            } else {
                configs.add(Pair.of(new Point(500, 1550), ESide.GAUCHE));
                configs.add(Pair.of(new Point(500, 1250), ESide.DROITE));
                configs.add(Pair.of(new Point(500, 950), ESide.GAUCHE));
            }

            for (Pair<Point, ESide> config : configs) {
                Point currentPosInMm = new Point(conv.pulseToMm(currentPosition.getPt().getX()),
                        conv.pulseToMm(currentPosition.getPt().getY()));

                double offsetAngle = NerellUtils.getAngleDecallagePince(currentPosInMm, config.getLeft(), config.getRight());
                double distance = currentPosInMm.distance(config.getLeft());

                mv.alignFrontToAvecDecalage(config.getLeft().getX(), config.getLeft().getY(), offsetAngle);
                mv.avanceMM(distance);
            }


        } catch (RefreshPathFindingException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());

        } finally {
            // si ça a échoué on a surement shooté dans les palets...
            completed = true;
        }
    }

}
