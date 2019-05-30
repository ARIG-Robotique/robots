package org.arig.robot.strategy.actions.active;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.exception.RefreshPathFindingException;
import org.arig.robot.model.Point;
import org.arig.robot.model.Position;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.Team;
import org.arig.robot.model.enums.CouleurPalet;
import org.arig.robot.strategy.AbstractAction;
import org.arig.robot.system.ICarouselManager;
import org.arig.robot.system.ITrajectoryManager;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.arig.robot.utils.SimpleCircularList;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Component
public class PrendreZoneChaosEquipe extends AbstractAction {

    @Autowired
    private ITrajectoryManager mv;

    @Autowired
    private ICarouselManager cm;

    @Autowired
    private RobotStatus rs;

    @Autowired
    private ConvertionRobotUnit conv;

    @Autowired
    @Qualifier("currentPosition")
    private Position currentPosition; // Attention ce sont des pulses

    @Getter
    private boolean completed = false;

    private static final int rayon = 180;

    @Override
    public String name() {
        return "Prise des palets de la zone de chaos de l'équipe";
    }

    @Override
    public int order() {
        return CouleurPalet.ROUGE.getImportance() * 2
                + CouleurPalet.VERT.getImportance()
                + CouleurPalet.BLEU.getImportance();
    }

    @Override
    public boolean isValid() {
        return isTimeValid() && cm.count(null) >= 3 && rs.getRemainingTime() > 40000;
    }

    @Override
    public void execute() {
        try {
            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);
            rs.enableAvoidance();

            final List<DistancePoint> points;
            if (rs.getTeam() == Team.VIOLET) {
                rs.setTrouNoirVioletVisite(true);
                points = definePointPassage(2000, 950);

            } else {
                rs.setTrouNoirJauneVisite(true);
                points = definePointPassage(1000, 950);
            }

            mv.pathTo(points.get(0).getPt().getX(), points.get(0).getPt().getY());
            mv.setVitesse(IConstantesNerellConfig.vitesseUltraLente / 2, IConstantesNerellConfig.vitesseOrientation);
            mv.pathTo(points.get(1).getPt().getX(), points.get(1).getPt().getY());
            mv.pathTo(points.get(3).getPt().getX(), points.get(3).getPt().getY());
            mv.pathTo(points.get(2).getPt().getX(), points.get(2).getPt().getY());
            mv.pathTo(points.get(0).getPt().getX(), points.get(0).getPt().getY());

            completed = true;
        } catch (NoPathFoundException | AvoidingException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
            updateValidTime();
        }

        try {
            if (!completed) {
                mv.reculeMM(50);
            }
        } catch (RefreshPathFindingException | AvoidingException e) {
            log.error("Erreur lors de la finalisation de l'action : {}", e.toString());
        }
    }

    private List<DistancePoint> definePointPassage(int x, int y) {
        // Position courante du Robot
        final Point positionRobot = new Point(
                conv.pulseToMm(currentPosition.getPt().getX()),
                conv.pulseToMm(currentPosition.getPt().getY())
        );

        List<DistancePoint> points = new ArrayList<>(4);
        points.add(new DistancePoint(new Point(x + rayon, y)));
        points.add(new DistancePoint(new Point(x - rayon, y)));
        points.add(new DistancePoint(new Point(x, y + rayon)));
        points.add(new DistancePoint(new Point(x, y - rayon)));
        points.forEach(p -> p.setDistance(p.getPt().distance(positionRobot)));
        points.sort(Comparator.comparingDouble(DistancePoint::getDistance));

        return points;
    }

    @Data
    @RequiredArgsConstructor
    private class DistancePoint {
        private final Point pt;
        private double distance;
    }
}
