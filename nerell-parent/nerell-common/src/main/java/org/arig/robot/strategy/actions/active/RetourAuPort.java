package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IEurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
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
        return "Retour au port";
    }

    @Override
    public Point entryPoint() {
        int offset = 575; // Empirique

        double x = 460;
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
            order = 5;
        } else {
            order = 10;
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
            rs.enablePincesAvant(); // Histoire de ne pas pousser une bouée qui va nous faire chié

            final Point entry = entryPoint();
            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
            mv.pathTo(entry, GotoOption.AVANT);
            setScore(coordProjection = true);

            // Finalisation de la rentré dans le port après avoir compter les points
            // TODO : Si c'est le premier robot
            Point finalPoint = new Point(entry);
            finalPoint.setX(205);
            if (rs.team() == ETeam.JAUNE) {
                finalPoint.setX(3000 - finalPoint.getX());
            }
            mv.gotoPoint(finalPoint, GotoOption.SANS_ORIENTATION);

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
