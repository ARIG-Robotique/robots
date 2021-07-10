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
        final int offset = 410; // Empirique
        final double x = getX(680);
        final double centerY = 1200;
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
            if (tableUtils.distance(entry) > 200) {
                mv.pathTo(entry, GotoOption.ARRIERE, GotoOption.SANS_ARRET_PASSAGE_ONLY_PATH);
            }
            group.port(port);

            // Finalisation de la rentrée dans le port après avoir compté les points
            if (!rs.otherPort().isInPort()) {
                // Premier arrivé on rentre dans la zone
                final double entryYFirst = port == EPort.NORD ? 1770 : 630;
                mv.gotoPoint(getX(160), entryYFirst, GotoOption.ARRIERE, GotoOption.SANS_ORIENTATION);
                mv.gotoOrientationDeg(rs.team() == ETeam.BLEU ? 0 : 180);
            } else {
                // Deuxieme arrivé, bisous Nerell
                mv.gotoPoint(getX(550), entry.getY(), GotoOption.ARRIERE);
                mv.gotoOrientationDeg(90);
                if (rs.team() == ETeam.BLEU) {
                    servosOdin.brasGaucheMancheAAir(false);
                } else {
                    servosOdin.brasDroitMancheAAir(false);
                }
            }
            rs.disableAsserv();

            complete();
        } catch (NoPathFoundException | AvoidingException e) {
            log.error("Erreur d'exécution de l'action : {}", e.toString());

            if (!rsOdin.inPort()) {
                group.port(EPort.AUCUN);
            }
        }
    }
}
