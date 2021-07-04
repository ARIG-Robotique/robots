package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IEurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.EPort;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.strategy.actions.AbstractNerellAction;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NerellRetourAuPort extends AbstractNerellAction {

    @Override
    public String name() {
        return IEurobotConfig.ACTION_RETOUR_AU_PORT_PREFIX + "Nerell";
    }

    @Override
    public Point entryPoint() {
        final int offset = 575; // Empirique
        final double x = getX(455);
        final double centerY = 1200;
        final Point north = new Point(x, centerY + offset);
        final Point south = new Point(x, centerY - offset);

        switch (rsNerell.otherPort()) {
            case NORD:
            case WIP_NORD:
                return north;

            case SUD:
            case WIP_SUD:
                return south;

            default:
                switch (rsNerell.directionGirouette()) {
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
        int order = rsNerell.twoRobots() ? 10 : 20;
        return order + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public boolean isValid() {
        return isTimeValid() && !rsNerell.inPort() && rsNerell.getRemainingTime() < IEurobotConfig.validRetourPortRemainingTimeNerell;
    }

    @Override
    public void execute() {
        try {
            // Histoire de ne pas pousser une bouée qui va nous faire chier
            rsNerell.enablePincesAvant();

            // Activation de la zone morte pour ne pas détecter l'autre robot
            if (rsNerell.team() == ETeam.BLEU) {
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

            // Finalisation de la rentrée dans le port après avoir compté les points
            if (rsNerell.otherPort() == EPort.AUCUN) {
                // Premier arrivé
                mv.pathTo(entry, GotoOption.ARRIERE, GotoOption.SANS_ARRET_PASSAGE_ONLY_PATH);
                group.port(port);
                mv.gotoPoint(getX(270), entry.getY(), GotoOption.SANS_ORIENTATION);

            } else {
                // Deuxieme arrivé
                final double entryYSecond = port == EPort.NORD ? 1775 : 640;
                mv.pathTo(entry.getX(), entryYSecond, GotoOption.AVANT, GotoOption.SANS_ARRET_PASSAGE_ONLY_PATH);
                group.port(port);
                mv.gotoOrientationDeg(rs.team() == ETeam.BLEU ? 180 : 0);
                if ((rs.team() == ETeam.BLEU && port == EPort.NORD) || (rs.team() == ETeam.JAUNE && port == EPort.SUD)) {
                    servosNerell.moustacheGaucheOuvert(false);
                } else {
                    servosNerell.moustacheDroiteOuvert(false);
                }
            }

            complete();
        } catch (NoPathFoundException | AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'exécution de l'action : {}", e.toString());

            if (!rsNerell.inPort()) {
                group.port(EPort.AUCUN);
            }
        }
    }
}
