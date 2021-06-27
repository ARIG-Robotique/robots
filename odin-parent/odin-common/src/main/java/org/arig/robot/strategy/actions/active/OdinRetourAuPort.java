package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IEurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.EPort;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.strategy.actions.AbstractOdinAction;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OdinRetourAuPort extends AbstractOdinAction {

    @Override
    public String name() {
        return IEurobotConfig.ACTION_RETOUR_AU_PORT_PREFIX + "Odin";
    }

    @Override
    public Point entryPoint() {
        int offset = 575; // Empirique

        double x = 550;
        double centerY = 1200;
        if (rsOdin.team() == ETeam.JAUNE) {
            x = 3000 - x;
        }
        final Point north = new Point(x, centerY + offset);
        final Point south = new Point(x, centerY - offset);

        switch (rsOdin.otherPort()) {
            case NORD:
            case WIP_NORD:
                return north;

            case SUD:
            case WIP_SUD:
                return south;

            default:
                switch (rsOdin.directionGirouette()) {
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
    }

    @Override
    public int order() {
        int order = rsOdin.twoRobots() ? 10 : 20;
        return order + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public boolean isValid() {
        return isTimeValid() && !rsOdin.inPort() && rsOdin.getRemainingTime() < IEurobotConfig.validRetourPortRemainingTimeOdin;
    }

    @Override
    public void execute() {
        try {
            // Histoire de ne pas pousser une bouée qui va nous faire chier
            rsOdin.enablePincesAvant();
            rsOdin.enablePincesArriere();

            // Activation de la zone morte pour ne pas détecter l'autre robot
            if (rsOdin.team() == ETeam.BLEU) {
                tableUtils.addDynamicDeadZone(new java.awt.Rectangle.Double(0, 500, 400, 400)); // Port SUD
                tableUtils.addDynamicDeadZone(new java.awt.Rectangle.Double(0, 1500, 400, 400)); // Port NORD
            } else {
                tableUtils.addDynamicDeadZone(new java.awt.Rectangle.Double(2600, 500, 400, 400)); // Port SUD
                tableUtils.addDynamicDeadZone(new java.awt.Rectangle.Double(2600, 1500, 400, 400)); // Port NORD
            }

            final Point entry = entryPoint();
            final EPort port = entry.getY() > 1200 ? EPort.NORD : EPort.SUD;
            group.port(port == EPort.NORD ? EPort.WIP_NORD : EPort.WIP_SUD);

            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
            mv.pathTo(entry);
            entry.setX(465);
            mv.gotoPoint(entry);

            group.port(port);

            // Finalisation de la rentrée dans le port après avoir compté les points
            if (rsOdin.otherPort() != port) {
                Point finalPoint = new Point(entry);
                finalPoint.setX(150);
                if (rsOdin.team() == ETeam.JAUNE) {
                    finalPoint.setX(3000 - finalPoint.getX());
                }
                mv.gotoPoint(finalPoint, GotoOption.SANS_ORIENTATION);
            }

        } catch (NoPathFoundException | AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'exécution de l'action : {}", e.toString());

            if (!rsOdin.inPort()) {
                group.port(EPort.AUCUN);
            }
        } finally {
            complete();
        }
    }
}
