package org.arig.robot.strategy.actions.active;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.model.Point;
import org.arig.robot.model.communication.balise.enums.DirectionGirouette;
import org.arig.robot.strategy.actions.AbstractNerellAction;
import org.arig.robot.system.ITrajectoryManager;
import org.arig.robot.utils.TableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RetourAuPort extends AbstractNerellAction {

    @Autowired
    private ITrajectoryManager mv;

    @Autowired
    private NerellRobotStatus rs;

    @Autowired
    private TableUtils tableUtils;

    @Getter
    private boolean completed = false;

    @Override
    public String name() {
        return "Retour au port";
    }

    @Override
    protected Point entryPoint() {
        int offset = 575; // Empirique

        double x = 460;
        double centerY = 1200;
        if (rs.getTeam() == ETeam.JAUNE) {
            x = 3000 - x;
        }
        final Point north = new Point(x, centerY + offset);
        final Point south = new Point(x, centerY - offset);

        switch (rs.getDirectionGirouette()) {
            case UP:
                return north;
            case DOWN:
                return south;
            default:
                // Inconnu, on prend le plus court
                double distanceNorth = tableUtils.distance(north);
                double distanceSouth = tableUtils.distance(south);
                return distanceNorth < distanceSouth ? north : south;
        }
    }

    @Override
    public int order() {
        int order;
        if (rs.getDirectionGirouette() == DirectionGirouette.UNKNOWN) {
            order = 5;
        } else {
            order = 10;
        }

        return order + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public boolean isValid() {
        return isTimeValid() && !rs.inPort() && rs.getRemainingTime() < IConstantesNerellConfig.validRetourPortRemainingTime;
    }

    @Override
    public void execute() {
        boolean coordProjection = false;
        try {
            final Point entry = entryPoint();
            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);
            mv.pathTo(entry);
            setScore(coordProjection = true);

            // Finalisation de la rentré dans le port après avoir compter les points
            Point finalPoint = new Point(entry);
            finalPoint.setX(215);
            if (rs.getTeam() == ETeam.JAUNE) {
                finalPoint.setX(3000 - finalPoint.getX());
            }
            mv.gotoPointMM(finalPoint, false);

        } catch (NoPathFoundException | AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
        } finally {
            completed = true;
            setScore(coordProjection);
        }
    }

    private void setScore(boolean coordProjection) {
        if (coordProjection && rs.getDirectionGirouette() != DirectionGirouette.UNKNOWN) {
            rs.bonPort(true);
        } else if (coordProjection) {
            rs.mauvaisPort(true);
        }
    }
}
