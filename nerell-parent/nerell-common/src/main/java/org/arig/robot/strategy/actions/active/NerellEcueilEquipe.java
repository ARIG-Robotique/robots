package org.arig.robot.strategy.actions.active;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IEurobotConfig;
import org.arig.robot.constants.INerellConstantesConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.services.INerellPincesArriereService;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class NerellEcueilEquipe extends AbstractNerellEcueil {

    public static final int ENTRY_X = 230;
    public static final int ENTRY_Y = 400;

    @Override
    public String name() {
        return IEurobotConfig.ACTION_ECUEIL_EQUIPE;
    }

    @Getter
    @Accessors(fluent = true)
    public List<String> blockingActions = Arrays.asList(
            IEurobotConfig.ACTION_MANCHE_A_AIR
    );

    @Override
    public Rectangle blockingZone() {
        return rsNerell.team() == ETeam.BLEU ? IEurobotConfig.ZONE_ECUEIL_EQUIPE_BLEU : IEurobotConfig.ZONE_ECUEIL_EQUIPE_JAUNE;
    }

    @Override
    public Point entryPoint() {
        return new Point(getX(ENTRY_X), ENTRY_Y);
    }

    @Override
    protected Point entryForCalage() {
        return new Point(getX(380), ENTRY_Y);
    }

    @Override
    protected void executeCalage() throws AvoidingException {
        mv.gotoPoint(entryForCalage().getX(), INerellConstantesConfig.dstCallage + 50, GotoOption.ARRIERE);

        mv.setVitesse(robotConfig.vitesse(30), robotConfig.vitesseOrientation());
        rs.enableCalageBordure();
        mv.reculeMMSansAngle(200);

        final double robotY = position.getPt().getY();
        final double realY = conv.mmToPulse(INerellConstantesConfig.dstCallage);
        //if (Math.abs(realY - robotY) > conv.mmToPulse(10)) {
            log.warn("RECALAGE REQUIS : yRobot = {} ; yReel = {}",
                    conv.pulseToMm(robotY), conv.pulseToMm(realY));

            position.getPt().setY(realY);
            position.setAngle(conv.degToPulse(90));
        //}

        mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());

        // Pour l'enchainement de la suite on sort de la bordure
        mv.avanceMM(50);
    }

    @Override
    protected byte nbBoueesDispo() {
        return 5;
    }

    @Override
    protected double orientationPourPrise() {
        return rsNerell.team() == ETeam.BLEU ? 0 : 180;
    }

    @Override
    protected INerellPincesArriereService.EEcueil ecueil() {
        return INerellPincesArriereService.EEcueil.EQUIPE;
    }

    @Override
    protected ECouleurBouee[] bouees() {
        return rsNerell.couleursEcueilEquipe();
    }

    @Override
    protected void onComplete() {
        group.ecueilEquipePris();
    }
}
