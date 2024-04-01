package org.arig.robot.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.arig.robot.model.EurobotStatus;
import org.arig.robot.model.bras.AnglesBras;
import org.arig.robot.model.bras.ConfigBras;
import org.arig.robot.model.bras.CurrentBras;
import org.arig.robot.model.bras.OptionBras;
import org.arig.robot.model.bras.PointBras;
import org.arig.robot.model.bras.PositionBras;
import org.arig.robot.model.bras.TransitionBras;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

import static org.arig.robot.services.AbstractCommonRobotServosService.*;

/**
 * API de bas niveau pour les bras
 */
@Slf4j
public abstract class BrasServiceInternal {

    private final AbstractCommonRobotServosService servos;
    protected final ThreadPoolExecutor executor;
    protected final EurobotStatus rs;

    ConfigBras CONFIG_BRAS_AVANT_GAUCHE = new ConfigBras(
            false,
            105, 261,
            73, 73, 110,
            true
    );
    ConfigBras CONFIG_BRAS_AVANT_CENTRE = new ConfigBras(
            false,
            105, 261,
            73, 73, 110,
            true
    );
    ConfigBras CONFIG_BRAS_AVANT_DROIT = new ConfigBras(
            false,
            105, 261,
            73, 73, 110,
            true
    );

    ConfigBras CONFIG_BRAS_ARRIERE_GAUCHE = new ConfigBras(
            true,
            105, 261,
            73, 73, 110,
            true
    );
    ConfigBras CONFIG_BRAS_ARRIERE_CENTRE = new ConfigBras(
            true,
            105, 261,
            73, 73, 110,
            true
    );
    ConfigBras CONFIG_BRAS_ARRIERE_DROIT = new ConfigBras(
            true,
            105, 261,
            73, 73, 110,
            true
    );

    @AllArgsConstructor
    public static class FullConfigBras {
        public ConfigBras config;
        public Set<PositionBras> states;
        public Set<Pair<PositionBras, PositionBras>> transitions;
    }

    private final BrasAvantGaucheStateMachine brasAvantGauche = new BrasAvantGaucheStateMachine(CONFIG_BRAS_AVANT_GAUCHE);
    private final BrasAvantCentreStateMachine brasAvantCentre = new BrasAvantCentreStateMachine(CONFIG_BRAS_AVANT_CENTRE);
    private final BrasAvantDroitStateMachine brasAvantDroit = new BrasAvantDroitStateMachine(CONFIG_BRAS_AVANT_DROIT);

    private final BrasArriereGaucheStateMachine brasArriereGauche = new BrasArriereGaucheStateMachine(CONFIG_BRAS_ARRIERE_GAUCHE);
    private final BrasArriereCentreStateMachine brasArriereCentre = new BrasArriereCentreStateMachine(CONFIG_BRAS_ARRIERE_CENTRE);
    private final BrasArriereDroitStateMachine brasArriereDroit = new BrasArriereDroitStateMachine(CONFIG_BRAS_ARRIERE_DROIT);

    private CurrentBras positionBrasAvantGauche;
    private CurrentBras positionBrasAvantCentre;
    private CurrentBras positionBrasAvantDroit;
    private CurrentBras positionBrasArriereGauche;
    private CurrentBras positionBrasArriereCentre;
    private CurrentBras positionBrasArriereDroit;

    public BrasServiceInternal(final AbstractCommonRobotServosService servos,
                               final ThreadPoolExecutor executor,
                               final EurobotStatus rs) {
        this.servos = servos;
        this.executor = executor;
        this.rs = rs;

        CONFIG_BRAS_AVANT_GAUCHE.a1Min = servos.servo(BRAS_AVANT_GAUCHE_EPAULE).angleMin();
        CONFIG_BRAS_AVANT_GAUCHE.a1Max = servos.servo(BRAS_AVANT_GAUCHE_EPAULE).angleMax();
        CONFIG_BRAS_AVANT_GAUCHE.a2Min = servos.servo(BRAS_AVANT_GAUCHE_COUDE).angleMin();
        CONFIG_BRAS_AVANT_GAUCHE.a2Max = servos.servo(BRAS_AVANT_GAUCHE_COUDE).angleMax();
        CONFIG_BRAS_AVANT_GAUCHE.a3Min = servos.servo(BRAS_AVANT_GAUCHE_POIGNET).angleMin();
        CONFIG_BRAS_AVANT_GAUCHE.a3Max = servos.servo(BRAS_AVANT_GAUCHE_POIGNET).angleMax();

        CONFIG_BRAS_AVANT_CENTRE.a1Min = servos.servo(BRAS_AVANT_CENTRE_EPAULE).angleMin();
        CONFIG_BRAS_AVANT_CENTRE.a1Max = servos.servo(BRAS_AVANT_CENTRE_EPAULE).angleMax();
        CONFIG_BRAS_AVANT_CENTRE.a2Min = servos.servo(BRAS_AVANT_CENTRE_COUDE).angleMin();
        CONFIG_BRAS_AVANT_CENTRE.a2Max = servos.servo(BRAS_AVANT_CENTRE_COUDE).angleMax();
        CONFIG_BRAS_AVANT_CENTRE.a3Min = servos.servo(BRAS_AVANT_CENTRE_POIGNET).angleMin();
        CONFIG_BRAS_AVANT_CENTRE.a3Max = servos.servo(BRAS_AVANT_CENTRE_POIGNET).angleMax();

        CONFIG_BRAS_AVANT_DROIT.a1Min = servos.servo(BRAS_AVANT_DROIT_EPAULE).angleMin();
        CONFIG_BRAS_AVANT_DROIT.a1Max = servos.servo(BRAS_AVANT_DROIT_EPAULE).angleMax();
        CONFIG_BRAS_AVANT_DROIT.a2Min = servos.servo(BRAS_AVANT_DROIT_COUDE).angleMin();
        CONFIG_BRAS_AVANT_DROIT.a2Max = servos.servo(BRAS_AVANT_DROIT_COUDE).angleMax();
        CONFIG_BRAS_AVANT_DROIT.a3Min = servos.servo(BRAS_AVANT_DROIT_POIGNET).angleMin();
        CONFIG_BRAS_AVANT_DROIT.a3Max = servos.servo(BRAS_AVANT_DROIT_POIGNET).angleMax();

        CONFIG_BRAS_ARRIERE_GAUCHE.a1Min = servos.servo(BRAS_ARRIERE_GAUCHE_EPAULE).angleMin();
        CONFIG_BRAS_ARRIERE_GAUCHE.a1Max = servos.servo(BRAS_ARRIERE_GAUCHE_EPAULE).angleMax();
        CONFIG_BRAS_ARRIERE_GAUCHE.a2Min = servos.servo(BRAS_ARRIERE_GAUCHE_COUDE).angleMin();
        CONFIG_BRAS_ARRIERE_GAUCHE.a2Max = servos.servo(BRAS_ARRIERE_GAUCHE_COUDE).angleMax();
        CONFIG_BRAS_ARRIERE_GAUCHE.a3Min = servos.servo(BRAS_ARRIERE_GAUCHE_POIGNET).angleMin();
        CONFIG_BRAS_ARRIERE_GAUCHE.a3Max = servos.servo(BRAS_ARRIERE_GAUCHE_POIGNET).angleMax();

        CONFIG_BRAS_ARRIERE_CENTRE.a1Min = servos.servo(BRAS_ARRIERE_CENTRE_EPAULE).angleMin();
        CONFIG_BRAS_ARRIERE_CENTRE.a1Max = servos.servo(BRAS_ARRIERE_CENTRE_EPAULE).angleMax();
        CONFIG_BRAS_ARRIERE_CENTRE.a2Min = servos.servo(BRAS_ARRIERE_CENTRE_COUDE).angleMin();
        CONFIG_BRAS_ARRIERE_CENTRE.a2Max = servos.servo(BRAS_ARRIERE_CENTRE_COUDE).angleMax();
        CONFIG_BRAS_ARRIERE_CENTRE.a3Min = servos.servo(BRAS_ARRIERE_CENTRE_POIGNET).angleMin();
        CONFIG_BRAS_ARRIERE_CENTRE.a3Max = servos.servo(BRAS_ARRIERE_CENTRE_POIGNET).angleMax();

        CONFIG_BRAS_ARRIERE_DROIT.a1Min = servos.servo(BRAS_ARRIERE_DROIT_EPAULE).angleMin();
        CONFIG_BRAS_ARRIERE_DROIT.a1Max = servos.servo(BRAS_ARRIERE_DROIT_EPAULE).angleMax();
        CONFIG_BRAS_ARRIERE_DROIT.a2Min = servos.servo(BRAS_ARRIERE_DROIT_COUDE).angleMin();
        CONFIG_BRAS_ARRIERE_DROIT.a2Max = servos.servo(BRAS_ARRIERE_DROIT_COUDE).angleMax();
        CONFIG_BRAS_ARRIERE_DROIT.a3Min = servos.servo(BRAS_ARRIERE_DROIT_POIGNET).angleMin();
        CONFIG_BRAS_ARRIERE_DROIT.a3Max = servos.servo(BRAS_ARRIERE_DROIT_POIGNET).angleMax();

        brasAvantGauche.onState(this::setBrasAvantGauche).build();
        brasAvantCentre.onState(this::setBrasAvantCentre).build();
        brasAvantDroit.onState(this::setBrasAvantDroit).build();
        brasArriereGauche.onState(this::setBrasArriereGauche).build();
        brasArriereCentre.onState(this::setBrasArriereCentre).build();
        brasArriereDroit.onState(this::setBrasArriereDroit).build();

        this.positionBrasAvantGauche = new CurrentBras(brasAvantGauche.current(), calculerBrasAvantGauche(brasAvantGauche.currentState()), brasAvantGauche.currentState());
        this.positionBrasAvantCentre = new CurrentBras(brasAvantCentre.current(), calculerBrasAvantCentre(brasAvantCentre.currentState()), brasAvantCentre.currentState());
        this.positionBrasAvantDroit = new CurrentBras(brasAvantDroit.current(), calculerBrasAvantDroit(brasAvantDroit.currentState()), brasAvantDroit.currentState());
        this.positionBrasArriereGauche = new CurrentBras(brasArriereGauche.current(), calculerBrasArriereGauche(brasArriereGauche.currentState()), brasArriereGauche.currentState());
        this.positionBrasArriereCentre = new CurrentBras(brasArriereCentre.current(), calculerBrasArriereCentre(brasArriereCentre.currentState()), brasArriereCentre.currentState());
        this.positionBrasArriereDroit = new CurrentBras(brasArriereDroit.current(), calculerBrasArriereDroit(brasArriereDroit.currentState()), brasArriereDroit.currentState());
    }

    public Map<String, FullConfigBras> getConfig() {
        return Map.of(
                "avantGauche", new FullConfigBras(CONFIG_BRAS_AVANT_GAUCHE, brasAvantGauche.states(), brasAvantGauche.transisions()),
                "avantCentre", new FullConfigBras(CONFIG_BRAS_AVANT_CENTRE, brasAvantCentre.states(), brasAvantCentre.transisions()),
                "avantDroit", new FullConfigBras(CONFIG_BRAS_AVANT_DROIT, brasAvantDroit.states(), brasAvantDroit.transisions()),
                "arriereGauche", new FullConfigBras(CONFIG_BRAS_ARRIERE_GAUCHE, brasArriereGauche.states(), brasArriereGauche.transisions()),
                "arriereCentre", new FullConfigBras(CONFIG_BRAS_ARRIERE_CENTRE, brasArriereCentre.states(), brasArriereCentre.transisions()),
                "arriereDroit", new FullConfigBras(CONFIG_BRAS_ARRIERE_DROIT, brasArriereCentre.states(), brasArriereCentre.transisions())
        );
    }

    public Map<String, CurrentBras> getCurrent() {
        return Map.of(
                "avantGauche", positionBrasAvantGauche,
                "avantCentre", positionBrasAvantCentre,
                "avantDroit", positionBrasAvantDroit,
                "arriereGauche", positionBrasArriereGauche,
                "arriereCentre", positionBrasArriereCentre,
                "arriereDroit", positionBrasArriereDroit
        );
    }

    public AnglesBras calculerBrasAvantGauche(PointBras pt) {
        return calculerAngles(CONFIG_BRAS_AVANT_GAUCHE, pt.x, pt.y, pt.a, false, null);
    }

    public AnglesBras calculerBrasAvantCentre(PointBras pt) {
        return calculerAngles(CONFIG_BRAS_AVANT_CENTRE, pt.x, pt.y, pt.a, false, null);
    }

    public AnglesBras calculerBrasAvantDroit(PointBras pt) {
        return calculerAngles(CONFIG_BRAS_AVANT_DROIT, pt.x, pt.y, pt.a, false, null);
    }

    public AnglesBras calculerBrasArriereGauche(PointBras pt) {
        return calculerAngles(CONFIG_BRAS_ARRIERE_GAUCHE, pt.x, pt.y, pt.a, false, null);
    }

    public AnglesBras calculerBrasArriereCentre(PointBras pt) {
        return calculerAngles(CONFIG_BRAS_ARRIERE_CENTRE, pt.x, pt.y, pt.a, false, null);
    }

    public AnglesBras calculerBrasArriereDroit(PointBras pt) {
        return calculerAngles(CONFIG_BRAS_ARRIERE_DROIT, pt.x, pt.y, pt.a, false, null);
    }

    /**
     * Change la position du bras en direct sans passer par la state machine
     */
    public boolean setBrasAvantGauche(PointBras pt, PositionBras state, int speed) {
        log.debug("Bras bas x={} y={} a={}", pt.x, pt.y, pt.a);
        AnglesBras angles = calculerAngles(CONFIG_BRAS_AVANT_GAUCHE, pt.x, pt.y, pt.a, true, null);

        if (angles == null || angles.isError()) {
            return false;
        }

        if (state == null) {
            brasAvantGauche.current(null);
        }

        log.debug("Bras bas a1={} a2={} a3={}", angles.a1, angles.a2, angles.a3);
        servos.brasAvantGauche(angles.a1, angles.a2, angles.a3, speed);
        positionBrasAvantGauche = new CurrentBras(state, angles, pt);

        return true;
    }

    /**
     * Change la position du bras bas en passant par la state machine
     */
    public void setBrasAvantGauche(PositionBras position) {
        setBrasAvantGauche(position, null);
    }

    public void setBrasAvantGauche(PositionBras position, OptionBras opt) {
        try {
            brasAvantGauche.goTo(position, opt);
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }

    /**
     * Change la position du bras en direct sans passer par la state machine
     */
    public boolean setBrasAvantCentre(PointBras pt, PositionBras state, int speed) {
        log.debug("Bras bas x={} y={} a={}", pt.x, pt.y, pt.a);
        AnglesBras angles = calculerAngles(CONFIG_BRAS_AVANT_CENTRE, pt.x, pt.y, pt.a, true, null);

        if (angles == null || angles.isError()) {
            return false;
        }

        if (state == null) {
            brasAvantCentre.current(null);
        }

        log.debug("Bras bas a1={} a2={} a3={}", angles.a1, angles.a2, angles.a3);
        servos.brasAvantCentre(angles.a1, angles.a2, angles.a3, speed);
        positionBrasAvantCentre = new CurrentBras(state, angles, pt);

        return true;
    }

    /**
     * Change la position du bras bas en passant par la state machine
     */
    public void setBrasAvantCentre(PositionBras position) {
        setBrasAvantCentre(position, null);
    }

    public void setBrasAvantCentre(PositionBras position, OptionBras opt) {
        try {
            brasAvantCentre.goTo(position, opt);
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }

    /**
     * Change la position du bras en direct sans passer par la state machine
     */
    public boolean setBrasAvantDroit(PointBras pt, PositionBras state, int speed) {
        log.debug("Bras bas x={} y={} a={}", pt.x, pt.y, pt.a);
        AnglesBras angles = calculerAngles(CONFIG_BRAS_AVANT_DROIT, pt.x, pt.y, pt.a, true, null);

        if (angles == null || angles.isError()) {
            return false;
        }

        if (state == null) {
            brasAvantDroit.current(null);
        }

        log.debug("Bras bas a1={} a2={} a3={}", angles.a1, angles.a2, angles.a3);
        servos.brasAvantDroit(angles.a1, angles.a2, angles.a3, speed);
        positionBrasAvantDroit = new CurrentBras(state, angles, pt);

        return true;
    }

    /**
     * Change la position du bras bas en passant par la state machine
     */
    public void setBrasAvantDroit(PositionBras position) {
        setBrasAvantDroit(position, null);
    }

    public void setBrasAvantDroit(PositionBras position, OptionBras opt) {
        try {
            brasAvantDroit.goTo(position, opt);
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }

    /**
     * Change la position du bras en direct sans passer par la state machine
     */
    public boolean setBrasArriereGauche(PointBras pt, PositionBras state, Integer speed) {
        log.debug("Bras haut x={} y={} a={}", pt.x, pt.y, pt.a);
        AnglesBras angles = calculerAngles(CONFIG_BRAS_ARRIERE_GAUCHE, pt.x, pt.y, pt.a, true, null);

        if (angles == null || angles.isError()) {
            return false;
        }

        if (state == null) {
            brasArriereGauche.current(null);
        }

        log.debug("Bras haut a1={} a2={} a3={}", angles.a1, angles.a2, angles.a3);
        servos.brasArriereGauche(angles.a1, angles.a2, angles.a3, speed);
        positionBrasArriereGauche = new CurrentBras(state, angles, pt);

        return true;
    }

    /**
     * Change la position du bras haut en passant par la state machine
     */
    public void setBrasArriereGauche(PositionBras position) {
        setBrasArriereGauche(position, null);
    }

    public void setBrasArriereGauche(PositionBras position, OptionBras opt) {
        try {
            brasArriereGauche.goTo(position, opt);
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }

    /**
     * Change la position du bras en direct sans passer par la state machine
     */
    public boolean setBrasArriereCentre(PointBras pt, PositionBras state, Integer speed) {
        log.debug("Bras haut x={} y={} a={}", pt.x, pt.y, pt.a);
        AnglesBras angles = calculerAngles(CONFIG_BRAS_ARRIERE_CENTRE, pt.x, pt.y, pt.a, true, null);

        if (angles == null || angles.isError()) {
            return false;
        }

        if (state == null) {
            brasArriereCentre.current(null);
        }

        log.debug("Bras haut a1={} a2={} a3={}", angles.a1, angles.a2, angles.a3);
        servos.brasArriereCentre(angles.a1, angles.a2, angles.a3, speed);
        positionBrasArriereCentre = new CurrentBras(state, angles, pt);

        return true;
    }

    /**
     * Change la position du bras haut en passant par la state machine
     */
    public void setBrasArriereCentre(PositionBras position) {
        setBrasArriereCentre(position, null);
    }

    public void setBrasArriereCentre(PositionBras position, OptionBras opt) {
        try {
            brasArriereCentre.goTo(position, opt);
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }

    /**
     * Change la position du bras en direct sans passer par la state machine
     */
    public boolean setBrasArriereDroit(PointBras pt, PositionBras state, Integer speed) {
        log.debug("Bras haut x={} y={} a={}", pt.x, pt.y, pt.a);
        AnglesBras angles = calculerAngles(CONFIG_BRAS_ARRIERE_DROIT, pt.x, pt.y, pt.a, true, null);

        if (angles == null || angles.isError()) {
            return false;
        }

        if (state == null) {
            brasArriereDroit.current(null);
        }

        log.debug("Bras haut a1={} a2={} a3={}", angles.a1, angles.a2, angles.a3);
        servos.brasArriereDroit(angles.a1, angles.a2, angles.a3, speed);
        positionBrasArriereDroit = new CurrentBras(state, angles, pt);

        return true;
    }

    /**
     * Change la position du bras haut en passant par la state machine
     */
    public void setBrasArriereDroit(PositionBras position) {
        setBrasArriereDroit(position, null);
    }

    public void setBrasArriereDroit(PositionBras position, OptionBras opt) {
        try {
            brasArriereDroit.goTo(position, opt);
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
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

    private void setBrasAvantGauche(PositionBras state, PointBras pt, TransitionBras transition, OptionBras opt) {
        for (PointBras point : transition.points()) {
            setBrasAvantGauche(point, state, opt == OptionBras.SLOW ? 80 : 100);
        }

        setBrasAvantGauche(pt, state, opt == OptionBras.SLOW ? 80 : 100);
    }

    private void setBrasAvantCentre(PositionBras state, PointBras pt, TransitionBras transition, OptionBras opt) {
        for (PointBras point : transition.points()) {
            setBrasAvantCentre(point, state, opt == OptionBras.SLOW ? 80 : 100);
        }

        setBrasAvantCentre(pt, state, opt == OptionBras.SLOW ? 80 : 100);
    }

    private void setBrasAvantDroit(PositionBras state, PointBras pt, TransitionBras transition, OptionBras opt) {
        for (PointBras point : transition.points()) {
            setBrasAvantDroit(point, state, opt == OptionBras.SLOW ? 80 : 100);
        }

        setBrasAvantDroit(pt, state, opt == OptionBras.SLOW ? 80 : 100);
    }

    private void setBrasArriereGauche(PositionBras state, PointBras pt, TransitionBras transition, OptionBras opt) {
        for (PointBras point : transition.points()) {
            setBrasArriereGauche(point, state, opt == OptionBras.SLOW ? 80 : 100);
        }

        setBrasArriereGauche(pt, state, opt == OptionBras.SLOW ? 80 : 100);
    }

    private void setBrasArriereCentre(PositionBras state, PointBras pt, TransitionBras transition, OptionBras opt) {
        for (PointBras point : transition.points()) {
            setBrasArriereCentre(point, state, opt == OptionBras.SLOW ? 80 : 100);
        }

        setBrasArriereCentre(pt, state, opt == OptionBras.SLOW ? 80 : 100);
    }

    private void setBrasArriereDroit(PositionBras state, PointBras pt, TransitionBras transition, OptionBras opt) {
        for (PointBras point : transition.points()) {
            setBrasArriereDroit(point, state, opt == OptionBras.SLOW ? 80 : 100);
        }

        setBrasArriereDroit(pt, state, opt == OptionBras.SLOW ? 80 : 100);
    }

    private AnglesBras calculerAngles(ConfigBras configBras, int x, int y, int a, boolean enableLog, Boolean preferA1Min) {
        boolean first = preferA1Min == null;

        if (preferA1Min == null) {
            preferA1Min = configBras.preferA1Min;
        }

        double a3Absolute = Math.toRadians(a);

        // Calcul de l'axe du servo moteur 3
        double xTemp = x - Math.cos(a3Absolute) * configBras.r3;
        double yTemp = y - Math.sin(a3Absolute) * configBras.r3;

        // Calcul de r
        double dX = xTemp - configBras.x;
        double dY = yTemp - configBras.y;
        double r = Math.sqrt(Math.pow(dX, 2) + Math.pow(dY, 2));

        if (r > configBras.r1 + configBras.r2) {
            if (enableLog) {
                log.warn("Impossible d'atteindre le point: r={} r1+r2={}", r, configBras.r1 + configBras.r2);
            }
            return null;
        }

        // Calcul de alpha1 (angle du servo moteur 1)
        double alpha3 = Math.atan2(dY, dX);
        double alpha4 = alKashiAngleRad(configBras.r1, r, configBras.r2);
        double alpha1 = alpha4 + alpha3;

        // Calcul de alpha2 (angle du servo moteur 2)
        double alpha6 = alKashiAngleRad(configBras.r2, configBras.r1, r);
        double alpha2 = alpha6 - Math.PI;

        // symétrise alpha1 et alpha2
        if (preferA1Min) {
            alpha1 -= (alpha1 - alpha3) * 2;
            alpha2 *= -1;
        }

        // Calcul de alpha3 (angle du servo moteur 3)
        int a3 = (int) Math.toDegrees(a3Absolute - (alpha1 + alpha2));
        if (a3 < configBras.a3Min && a3 + 360 <= configBras.a3Max) {
            a3 += 360;
        }
        if (a3 > configBras.a3Max && a3 - 360 >= configBras.a3Min) {
            a3 -= 360;
        }

        AnglesBras result = new AnglesBras(
                (int) Math.toDegrees(alpha1),
                (int) Math.toDegrees(alpha2),
                a3
        );

        result.a1Error = result.a1 < configBras.a1Min || result.a1 > configBras.a1Max;
        result.a2Error = result.a2 < configBras.a2Min || result.a2 > configBras.a2Max;
        result.a3Error = result.a3 < configBras.a3Min || result.a3 > configBras.a3Max;

        // si l'inversion entraine une erreur, on essaye sans inversion
        if (first && result.isError()) {
            AnglesBras newResult = calculerAngles(configBras, x, y, a, false, !preferA1Min);
            if (newResult != null && !newResult.isError()) {
                return newResult;
            }
        }

        if (enableLog) {
            if (result.a1Error) {
                log.warn("Impossible d'atteindre le point: a1={} a1Min={} a1Max={}", result.a1, configBras.a1Min, configBras.a1Max);
            }
            if (result.a2Error) {
                log.warn("Impossible d'atteindre le point: a2={} a2Min={} a2Max={}", result.a2, configBras.a2Min, configBras.a2Max);
            }
            if (result.a3Error) {
                log.warn("Impossible d'atteindre le point: a3={} a3Min={} a3Max={}", result.a3, configBras.a3Min, configBras.a3Max);
            }
        }

        return result;
    }

    private double alKashiAngleRad(double a, double b, double c) {
        return Math.acos((Math.pow(a, 2) + Math.pow(b, 2) - Math.pow(c, 2)) / (2 * a * b));
    }

}
