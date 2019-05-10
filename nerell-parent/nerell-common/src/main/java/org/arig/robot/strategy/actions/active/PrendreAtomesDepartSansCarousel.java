package org.arig.robot.strategy.actions.active;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.model.*;
import org.arig.robot.strategy.AbstractAction;
import org.arig.robot.system.ITrajectoryManager;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.arig.robot.utils.NerellUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class PrendreAtomesDepartSansCarousel extends AbstractAction {

    @Autowired
    private ITrajectoryManager mv;

    @Autowired
    private RobotStatus rs;

    @Autowired
    @Qualifier("currentPosition")
    private Position currentPosition;

    @Autowired
    private ConvertionRobotUnit convertionRobotUnit;

    @Getter
    private boolean completed = false;

    @Override
    public String name() {
        return "Prise des atomes de départ sans le caroussel";
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public int order() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void execute() {
        mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);

        try {
            rs.disableAvoidance();

            List<Pair<Point, ESide>> configs = new ArrayList<>();

            if (rs.getTeam().equals(Team.VIOLET)) {
                configs.add(Pair.of(new Point(2500, 1550), ESide.DROITE));
                configs.add(Pair.of(new Point(2500, 1250), ESide.GAUCHE));
            } else {
                configs.add(Pair.of(new Point(500, 1550), ESide.GAUCHE));
                configs.add(Pair.of(new Point(500, 1250), ESide.DROITE));
            }

            log.info("current position (x,y,angle) ({},{},{})", convertionRobotUnit.pulseToMm(currentPosition.getPt().getX()), convertionRobotUnit.pulseToMm(currentPosition.getPt().getY()), currentPosition.getAngle());

            for (Pair<Point, ESide> config : configs) {
                Point currentPosInMm = new Point(convertionRobotUnit.pulseToMm(currentPosition.getPt().getX()),
                        convertionRobotUnit.pulseToMm(currentPosition.getPt().getY()));

                double offsetAngle = NerellUtils.getAngleDecallagePince(currentPosInMm, config.getLeft(), config.getRight());
                double distance = currentPosInMm.distance(config.getLeft());

                mv.alignFrontToAvecDecalage(config.getLeft().getX(), config.getLeft().getY(), offsetAngle);
                mv.avanceMM(distance);
            }
        } catch (Exception e) {

        } finally {
            completed = true;
        }
    }
}
