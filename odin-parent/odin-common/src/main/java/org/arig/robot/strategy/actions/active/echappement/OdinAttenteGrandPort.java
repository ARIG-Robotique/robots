package org.arig.robot.strategy.actions.active.echappement;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IEurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.Point;
import org.arig.robot.strategy.actions.AbstractOdinAction;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class OdinAttenteGrandPort extends AbstractOdinAction {

    @Override
    public Point entryPoint() {
        return new Point(getX(600), 1200);
    }

    @Override
    public String name() {
        return IEurobotConfig.ACTION_ATTENTE_GRAND_PORT_PREFIX + "Odin";
    }

    @Getter
    @Accessors(fluent = true)
    public List<String> blockingActions = Arrays.asList(
            IEurobotConfig.ACTION_DEPOSE_GRAND_PORT,
            IEurobotConfig.ACTION_DEPOSE_GRAND_PORT_VERT,
            IEurobotConfig.ACTION_DEPOSE_GRAND_PORT_ROUGE
    );

    @Override
    public Rectangle blockingZone() {
        return rsOdin.team() == ETeam.BLEU ? IEurobotConfig.ZONE_GRAND_PORT_BLEU : IEurobotConfig.ZONE_GRAND_PORT_JAUNE;
    }

    @Override
    public int order() {
        return -100;
    }

    @Override
    public boolean isValid() {
        return isTimeValid() && !rsOdin.inPort();
    }

    @Override
    public void execute() {
        try {
            rs.enableAvoidance(); // Pour eviter de se rentrer dans l'autre robot
            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
            mv.pathTo(entryPoint());
        } catch (AvoidingException | NoPathFoundException e) {
            updateValidTime();
            log.error("Erreur exécution de l'action : {}", e.toString());
        }
    }
}
