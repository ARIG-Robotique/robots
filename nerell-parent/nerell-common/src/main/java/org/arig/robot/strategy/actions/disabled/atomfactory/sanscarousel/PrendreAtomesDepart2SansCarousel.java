package org.arig.robot.strategy.actions.disabled.atomfactory.sanscarousel;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.model.ESide;
import org.arig.robot.model.Point;
import org.arig.robot.model.Position;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.Team;
import org.arig.robot.model.enums.CouleurPalet;
import org.arig.robot.services.VentousesService;
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
public class PrendreAtomesDepart2SansCarousel extends AbstractAction {

    @Autowired
    private ITrajectoryManager mv;

    @Autowired
    private RobotStatus rs;

    @Autowired
    @Qualifier("currentPosition")
    private Position currentPosition;

    @Autowired
    private ConvertionRobotUnit convertionRobotUnit;

    @Autowired
    private VentousesService ventouses;

    @Getter
    private boolean completed = false;

    @Override
    public String name() {
        return "Prise des atomes de départ sans le caroussel";
    }

    @Override
    public boolean isValid() {
        return ventouses.getCouleur(ESide.GAUCHE) == null && ventouses.getCouleur(ESide.DROITE) == null;
    }

    @Override
    public int order() {
        return Integer.MAX_VALUE - 1;
    }

    @Override
    public void execute() {
        mv.setVitesse(IConstantesNerellConfig.vitesseMouvement, IConstantesNerellConfig.vitesseOrientation);

        try {
            rs.disableAvoidance();

            List<Pair<Point, ESide>> configs = new ArrayList<>();

            if (rs.getTeam().equals(Team.VIOLET)) {
                configs.add(Pair.of(new Point(2500, 950), ESide.DROITE));
            } else {
                configs.add(Pair.of(new Point(500, 950), ESide.GAUCHE));
            }

            for (Pair<Point, ESide> config : configs) {
                Point currentPosInMm = new Point(convertionRobotUnit.pulseToMm(currentPosition.getPt().getX()),
                        convertionRobotUnit.pulseToMm(currentPosition.getPt().getY()));

                double offsetAngle = NerellUtils.getAngleDecallagePince(currentPosInMm, config.getLeft(), config.getRight());
                double distance = currentPosInMm.distance(config.getLeft());

                mv.alignFrontToAvecDecalage(config.getLeft().getX(), config.getLeft().getY(), offsetAngle);
                mv.avanceMM(distance);

                if (ventouses.priseTable(CouleurPalet.INCONNU, config.getRight())) {
                    ventouses.stockageAsync(config.getRight());
                } else {
                    ventouses.finishDeposeAsync(config.getRight());
                }
            }
        } catch (Exception e) {

        } finally {
            completed = true;
        }
    }
}
