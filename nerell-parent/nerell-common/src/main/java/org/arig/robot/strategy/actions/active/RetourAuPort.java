package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IEurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.EPort;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.Point;
import org.arig.robot.model.communication.balise.enums.EDirectionGirouette;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.strategy.actions.AbstractNerellAction;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RetourAuPort extends AbstractNerellAction {

    @Override
    public String name() {
        return IEurobotConfig.ACTION_RETOUR_AU_PORT;
    }

    @Override
    public Point entryPoint() {
        int offset = 575; // Empirique

        double x = 460; // TODO Odin 440
        double centerY = 1200;
        if (rs.team() == ETeam.JAUNE) {
            x = 3000 - x;
        }
        final Point north = new Point(x, centerY + offset);
        final Point south = new Point(x, centerY - offset);

        switch (rs.directionGirouette()) {
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
        if (rs.directionGirouette() == EDirectionGirouette.UNKNOWN) {
            order = rs.twoRobots() ? 3 : 6;
        } else {
            order = rs.twoRobots() ? 10 : 20;
        }

        return order + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public boolean isValid() {
        return isTimeValid() && !rs.inPort() && rs.getRemainingTime() < IEurobotConfig.validRetourPortRemainingTime;
    }

    @Override
    public void execute() {
        boolean coordProjection = false;
        try {
            rs.enablePincesAvant(); // Histoire de ne pas pousser une bouée qui va nous faire chier

            final Point entry = entryPoint();
            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
            mv.pathTo(entry, GotoOption.AVANT);
            setScore(coordProjection = true);

            // Finalisation de la rentrée dans le port après avoir compté les points
            if (rs.otherPort() == EPort.AUCUN) {
                Point finalPoint = new Point(entry);
                finalPoint.setX(150);
                if (rs.team() == ETeam.JAUNE) {
                    finalPoint.setX(3000 - finalPoint.getX());
                }
                mv.gotoPoint(finalPoint, GotoOption.SANS_ORIENTATION);
            }

        } catch (NoPathFoundException | AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'exécution de l'action : {}", e.toString());
        } finally {
            complete();
            setScore(coordProjection);
        }
    }

    private void setScore(boolean coordProjection) {
        if (coordProjection && rs.directionGirouette() != EDirectionGirouette.UNKNOWN) {
            group.bonPort();
        } else if (coordProjection) {
            group.mauvaisPort();
        }
    }
}
