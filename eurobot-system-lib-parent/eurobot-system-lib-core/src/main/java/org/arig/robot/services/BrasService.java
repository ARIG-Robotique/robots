package org.arig.robot.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.arig.robot.model.Bras;
import org.arig.robot.model.EurobotStatus;
import org.arig.robot.model.RobotConfig;
import org.arig.robot.model.bras.AnglesBras;
import org.arig.robot.model.bras.ConfigBras;
import org.arig.robot.model.bras.CurrentBras;
import org.arig.robot.model.bras.OptionBras;
import org.arig.robot.model.bras.PointBras;
import org.arig.robot.model.bras.PositionBras;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

import static org.arig.robot.services.AbstractCommonRobotServosService.*;

@Slf4j
@Service
public class BrasService {

    private final RobotConfig config;
    private final CommonRobotIOService io;
    private final AbstractCommonRobotServosService servos;
    private final ThreadPoolExecutor executor;
    private final EurobotStatus rs;

    @AllArgsConstructor
    public static class FullConfigBras {
        public ConfigBras config;
        public Set<PositionBras> states;
        public Set<Pair<PositionBras, PositionBras>> transitions;
    }

    private final BrasStateMachine brasAvantGauche;
    private final BrasStateMachine brasAvantCentre;
    private final BrasStateMachine brasAvantDroit;
    private final BrasStateMachine brasArriereGauche;
    private final BrasStateMachine brasArriereCentre;
    private final BrasStateMachine brasArriereDroit;

    public BrasService(final AbstractCommonRobotServosService servos,
                       final ThreadPoolExecutor executor,
                       final RobotConfig config,
                       final EurobotStatus rs,
                       final CommonRobotIOService io) {
        this.servos = servos;
        this.config = config;
        this.io = io;
        this.executor = executor;
        this.rs = rs;

        brasAvantGauche = new BrasStateMachine("Avant gauche", false, servos::brasAvantGauche);
        brasAvantCentre = new BrasStateMachine("Avant centre", false, servos::brasAvantCentre);
        brasAvantDroit = new BrasStateMachine("Avant droit", false, servos::brasAvantDroit);
        brasArriereGauche = new BrasStateMachine("Arrière gauche", true, servos::brasArriereGauche);
        brasArriereCentre = new BrasStateMachine("Arrière centre", true, servos::brasArriereCentre);
        brasArriereDroit = new BrasStateMachine("Arrière droit", true, servos::brasArriereDroit);
    }

    public Map<Bras, FullConfigBras> getConfig() {
        return Map.of(
                Bras.AVANT_GAUCHE, new FullConfigBras(brasAvantGauche.config(), brasAvantGauche.states(), brasAvantGauche.transisions()),
                Bras.AVANT_CENTRE, new FullConfigBras(brasAvantCentre.config(), brasAvantCentre.states(), brasAvantCentre.transisions()),
                Bras.AVANT_DROIT, new FullConfigBras(brasAvantDroit.config(), brasAvantDroit.states(), brasAvantDroit.transisions()),
                Bras.ARRIERE_GAUCHE, new FullConfigBras(brasArriereGauche.config(), brasArriereGauche.states(), brasArriereGauche.transisions()),
                Bras.ARRIERE_CENTRE, new FullConfigBras(brasArriereCentre.config(), brasArriereCentre.states(), brasArriereCentre.transisions()),
                Bras.ARRIERE_DROIT, new FullConfigBras(brasArriereDroit.config(), brasArriereDroit.states(), brasArriereDroit.transisions())
        );
    }

    public Map<Bras, CurrentBras> getCurrent() {
        return Map.of(
                Bras.AVANT_GAUCHE, brasAvantGauche.current(),
                Bras.AVANT_CENTRE, brasAvantCentre.current(),
                Bras.AVANT_DROIT, brasAvantDroit.current(),
                Bras.ARRIERE_GAUCHE, brasArriereGauche.current(),
                Bras.ARRIERE_CENTRE, brasArriereCentre.current(),
                Bras.ARRIERE_DROIT, brasArriereDroit.current()
        );
    }

    public AnglesBras calculerBras(Bras bras, PointBras pt) {
        return switch (bras) {
            case AVANT_GAUCHE -> brasAvantGauche.calculerAngles(pt.x, pt.y, pt.a, pt.invertA1, false);
            case AVANT_CENTRE -> brasAvantCentre.calculerAngles(pt.x, pt.y, pt.a, pt.invertA1, false);
            case AVANT_DROIT -> brasAvantDroit.calculerAngles(pt.x, pt.y, pt.a, pt.invertA1, false);
            case ARRIERE_GAUCHE -> brasArriereGauche.calculerAngles(pt.x, pt.y, pt.a, pt.invertA1, false);
            case ARRIERE_CENTRE -> brasArriereCentre.calculerAngles(pt.x, pt.y, pt.a, pt.invertA1, false);
            case ARRIERE_DROIT -> brasArriereDroit.calculerAngles(pt.x, pt.y, pt.a, pt.invertA1, false);
        };
    }

    /**
     * Change la position du bras en direct sans passer par la state machine
     */
    public boolean setBras(Bras bras, PointBras pt, PositionBras state, int speed) {
        return switch (bras) {
            case AVANT_GAUCHE -> brasAvantGauche.set(pt, state, speed);
            case AVANT_CENTRE -> brasAvantCentre.set(pt, state, speed);
            case AVANT_DROIT -> brasAvantDroit.set(pt, state, speed);
            case ARRIERE_GAUCHE -> brasArriereGauche.set(pt, state, speed);
            case ARRIERE_CENTRE -> brasArriereCentre.set(pt, state, speed);
            case ARRIERE_DROIT -> brasArriereDroit.set(pt, state, speed);
        };
    }

    public void setBrasAvant(PositionBras positionBras) {
        setBras(Bras.AVANT_GAUCHE, positionBras, OptionBras.NO_WAIT);
        setBras(Bras.AVANT_CENTRE, positionBras, OptionBras.NO_WAIT);
        setBras(Bras.AVANT_DROIT, positionBras, null);
    }

    public void setBrasArriere(PositionBras positionBras) {
        setBras(Bras.ARRIERE_GAUCHE, positionBras, OptionBras.NO_WAIT);
        setBras(Bras.ARRIERE_CENTRE, positionBras, OptionBras.NO_WAIT);
        setBras(Bras.ARRIERE_DROIT, positionBras, null);
    }

    /**
     * Change la position du bras en passant par la state machine
     */
    public void setBras(Bras bras, PositionBras position, OptionBras opt) {
        switch (bras) {
            case AVANT_GAUCHE -> brasAvantGauche.goTo(position, opt);
            case AVANT_CENTRE -> brasAvantCentre.goTo(position, opt);
            case AVANT_DROIT -> brasAvantDroit.goTo(position, opt);
            case ARRIERE_GAUCHE -> brasArriereGauche.goTo(position, opt);
            case ARRIERE_CENTRE -> brasArriereCentre.goTo(position, opt);
            case ARRIERE_DROIT -> brasArriereDroit.goTo(position, opt);
        }
    }

    /**
     * Depuis n'importe quelle position, repasse en position de repos
     */
    public void safeHoming() {
        boolean brasAvantGaucheDisableCheck = brasAvantGauche.disableCheck();
        boolean brasAvantCentreDisableCheck = brasAvantCentre.disableCheck();
        boolean brasAvantDroitDisableCheck = brasAvantDroit.disableCheck();
        boolean brasArriereGaucheDisableCheck = brasArriereGauche.disableCheck();
        boolean brasArriereCentreDisableCheck = brasArriereCentre.disableCheck();
        boolean brasArriereDroitDisableCheck = brasArriereDroit.disableCheck();

        brasAvantGauche.disableCheck(true);
        brasAvantCentre.disableCheck(true);
        brasAvantDroit.disableCheck(true);
        brasArriereGauche.disableCheck(true);
        brasArriereCentre.disableCheck(true);
        brasArriereDroit.disableCheck(true);

        brasAvantGauche.goTo(PositionBras.INIT);
        brasAvantCentre.goTo(PositionBras.INIT);
        brasAvantDroit.goTo(PositionBras.INIT);
        brasArriereGauche.goTo(PositionBras.INIT);
        brasArriereCentre.goTo(PositionBras.INIT);
        brasArriereDroit.goTo(PositionBras.INIT);

        brasAvantGauche.disableCheck(brasAvantGaucheDisableCheck);
        brasAvantCentre.disableCheck(brasAvantCentreDisableCheck);
        brasAvantDroit.disableCheck(brasAvantDroitDisableCheck);
        brasArriereGauche.disableCheck(brasArriereGaucheDisableCheck);
        brasArriereCentre.disableCheck(brasArriereCentreDisableCheck);
        brasArriereDroit.disableCheck(brasArriereDroitDisableCheck);
    }

}
