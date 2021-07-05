package org.arig.robot.strategy.actions.active;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IEurobotConfig;
import org.arig.robot.constants.IOdinConstantesConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.strategy.actions.AbstractOdinAction;
import org.arig.robot.utils.ThreadUtils;
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
        return rs.echangeReady() &&
                rs.getRemainingTime() > IEurobotConfig.invalidPriseRemainingTime &&
                rsOdin.pincesAvantEmpty() && rsOdin.pincesArriereEmpty();
    }

    @Override
    public void refreshCompleted() {
        if (!rs.echangeEcueil() || rs.groupOk()) {
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
            mv.gotoOrientationDeg(rs.team() == ETeam.BLEU ? 95 : -95);
            mv.gotoPoint(getX(450), 200, rs.team() == ETeam.BLEU ? GotoOption.ARRIERE : GotoOption.AVANT);
            mv.gotoOrientationDeg(rs.team() == ETeam.BLEU ? 175 : -175);
            mv.gotoPoint(getX(180), 230, rs.team() == ETeam.BLEU ? GotoOption.AVANT : GotoOption.ARRIERE);
            ThreadUtils.sleep(IOdinConstantesConfig.WAIT_POMPES);
            mv.gotoPoint(getX(230), 230, rs.team() == ETeam.BLEU ? GotoOption.ARRIERE : GotoOption.AVANT);

            complete();

        } catch (NoPathFoundException | AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'exécution de l'action : {}", e.toString());
        }
    }
}
