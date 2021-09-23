package org.arig.robot.strategy.actions.active;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IEurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.model.enums.TypeCalage;
import org.arig.robot.strategy.actions.AbstractOdinAction;
import org.springframework.stereotype.Component;

import java.awt.Rectangle;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class OdinEchangeEcueil extends AbstractOdinAction {

    @Override
    public String name() {
        return IEurobotConfig.ACTION_ECHANGE_ECUEIL;
    }

    @Getter
    @Accessors(fluent = true)
    public List<String> blockingActions = Arrays.asList(
            IEurobotConfig.ACTION_MANCHE_A_AIR
    );

    @Override
    public Rectangle blockingZone() {
        return rs.team() == ETeam.BLEU ? IEurobotConfig.ZONE_ECUEIL_EQUIPE_BLEU : IEurobotConfig.ZONE_ECUEIL_EQUIPE_JAUNE;
    }

    @Override
    public boolean isValid() {
        return isTimeValid() && !rsOdin.inPort() && rs.echangeReady() &&
                rs.getRemainingTime() > IEurobotConfig.invalidPriseRemainingTime &&
                rsOdin.pincesAvantEmpty() && rsOdin.pincesArriereEmpty();
    }

    @Override
    public void refreshCompleted() {
        if (!rs.echangeEcueil() || !rs.groupOk()) {
            complete();
        }
    }

    @Override
    public int order() {
        return 500;
    }

    @Override
    public Point entryPoint() {
        return new Point(getX(570), 730);
    }

    @Override
    public void execute() {
        try {
            rsOdin.enablePincesArriere();
            rsOdin.enablePincesAvant();

            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
            mv.pathTo(entryPoint());

            mv.setVitesse(robotConfig.vitesse(20), robotConfig.vitesseOrientation());

            mv.gotoPoint(getX(400), 650);
            mv.gotoPoint(getX(450), 200, GotoOption.ARRIERE);
            mv.gotoPoint(getX(160), 230, GotoOption.AVANT);
            rs.enableCalageBordure(TypeCalage.CUSTOM); // calage sur présence ventouses
            mv.setVitesse(robotConfig.vitesse(10), robotConfig.vitesseOrientation());
            mv.avanceMMSansAngle(100);
            mv.gotoPoint(getX(230), 230, GotoOption.ARRIERE);

            complete();

        } catch (NoPathFoundException | AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'exécution de l'action : {}", e.toString());
        }
    }
}
