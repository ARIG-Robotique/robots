package org.arig.robot.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.arig.robot.model.EurobotStatus;
import org.arig.robot.model.bras.AnglesBras;
import org.arig.robot.model.bras.ConfigBras;
import org.arig.robot.model.bras.CurrentBras;
import org.arig.robot.model.bras.PointBras;
import org.arig.robot.model.bras.PositionBras;
import org.arig.robot.model.bras.TransitionBras;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

import static org.arig.robot.services.AbstractCommonServosService.*;

/**
 * API de bas niveau pour les bras
 */
@Slf4j
public abstract class BrasServiceInternal {

    private final AbstractCommonServosService servos;
    protected final ThreadPoolExecutor executor;
    protected final EurobotStatus rs;

    ConfigBras CONFIG_BRAS_BAS = new ConfigBras(
            54, 51,
            64, 71, 35,
            true
    );

    ConfigBras CONFIG_BRAS_HAUT = new ConfigBras(
            54, 261,
            64, 71, 35,
            false
    );

    @AllArgsConstructor
    public static class AllConfigBras {
        public ConfigBras bas;
        public ConfigBras haut;
        public Set<PositionBras> statesBas;
        public Set<PositionBras> statesHaut;
        public Set<Pair<PositionBras, PositionBras>> transitionsBas;
        public Set<Pair<PositionBras, PositionBras>> transitionsHaut;
    }

    private final BrasBasStateMachine brasBas = new BrasBasStateMachine(CONFIG_BRAS_BAS);
    private final BrasHautStateMachine brasHaut = new BrasHautStateMachine(CONFIG_BRAS_HAUT);

    private CurrentBras positionBrasBas;
    private CurrentBras positionBrasHaut;

    public BrasServiceInternal(final AbstractCommonServosService servos,
                               final ThreadPoolExecutor executor,
                               final EurobotStatus rs) {
        this.servos = servos;
        this.executor = executor;
        this.rs = rs;

        CONFIG_BRAS_BAS.a1Min = servos.servo(BRAS_BAS_EPAULE).angleMin();
        CONFIG_BRAS_BAS.a1Max = servos.servo(BRAS_BAS_EPAULE).angleMax();
        CONFIG_BRAS_BAS.a2Min = servos.servo(BRAS_BAS_COUDE).angleMin();
        CONFIG_BRAS_BAS.a2Max = servos.servo(BRAS_BAS_COUDE).angleMax();
        CONFIG_BRAS_BAS.a3Min = servos.servo(BRAS_BAS_POIGNET).angleMin();
        CONFIG_BRAS_BAS.a3Max = servos.servo(BRAS_BAS_POIGNET).angleMax();
        CONFIG_BRAS_HAUT.a1Min = servos.servo(BRAS_HAUT_EPAULE).angleMin();
        CONFIG_BRAS_HAUT.a1Max = servos.servo(BRAS_HAUT_EPAULE).angleMax();
        CONFIG_BRAS_HAUT.a2Min = servos.servo(BRAS_HAUT_COUDE).angleMin();
        CONFIG_BRAS_HAUT.a2Max = servos.servo(BRAS_HAUT_COUDE).angleMax();
        CONFIG_BRAS_HAUT.a3Min = servos.servo(BRAS_HAUT_POIGNET).angleMin();
        CONFIG_BRAS_HAUT.a3Max = servos.servo(BRAS_HAUT_POIGNET).angleMax();

        brasBas.onState(this::setBrasBas).build();
        brasHaut.onState(this::setBrasHaut).build();

        this.positionBrasBas = new CurrentBras(brasBas.current(), calculerBrasBas(brasBas.currentState()), brasBas.currentState());
        this.positionBrasHaut = new CurrentBras(brasHaut.current(), calculerBrasHaut(brasHaut.currentState()), brasHaut.currentState());
    }

    public AllConfigBras getConfig() {
        return new AllConfigBras(CONFIG_BRAS_BAS, CONFIG_BRAS_HAUT, brasBas.states(), brasHaut.states(), brasBas.transisions(), brasHaut.transisions());
    }

    public Map<String, CurrentBras> getCurrent() {
        return Map.of("bas", positionBrasBas, "haut", positionBrasHaut);
    }

    public AnglesBras calculerBrasBas(PointBras pt) {
        return calculerAngles(CONFIG_BRAS_BAS, pt.x, pt.y, pt.a, false, null);
    }

    public AnglesBras calculerBrasHaut(PointBras pt) {
        return calculerAngles(CONFIG_BRAS_HAUT, pt.x, pt.y, pt.a, false, null);
    }

    /**
     * Change la position du bras en direct sans passer par la state machine
     */
    public boolean setBrasBas(PointBras pt, PositionBras state, int speed) {
        log.debug("Bras bas x={} y={} a={}", pt.x, pt.y, pt.a);
        AnglesBras angles = calculerAngles(CONFIG_BRAS_BAS, pt.x, pt.y, pt.a, true, null);

        if (angles == null || angles.isError()) {
            return false;
        }

        if (state == null) {
            brasBas.current(null);
        }

        log.debug("Bras bas a1={} a2={} a3={}", angles.a1, angles.a2, angles.a3);
        servos.brasBas(angles.a1, angles.a2, angles.a3, speed);
        positionBrasBas = new CurrentBras(state, angles, pt);

        return true;
    }

    /**
     * Change la position du bras bas en passant par la state machine
     */
    public void setBrasBas(PositionBras position) {
        try {
            brasBas.goTo(position);
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }

    public CompletableFuture<Void> setBrasBasAsync(PositionBras position) {
        return CompletableFuture.runAsync(() -> setBrasBas(position), executor);
    }

    /**
     * Change la position du bras en direct sans passer par la state machine
     */
    public boolean setBrasHaut(PointBras pt, PositionBras state, Integer speed) {
        log.debug("Bras haut x={} y={} a={}", pt.x, pt.y, pt.a);
        AnglesBras angles = calculerAngles(CONFIG_BRAS_HAUT, pt.x, pt.y, pt.a, true, null);

        if (angles == null || angles.isError()) {
            return false;
        }

        if (state == null) {
            brasHaut.current(null);
        }

        log.debug("Bras haut a1={} a2={} a3={}", angles.a1, angles.a2, angles.a3);
        servos.brasHaut(angles.a1, angles.a2, angles.a3, speed);
        positionBrasHaut = new CurrentBras(state, angles, pt);

        return true;
    }

    /**
     * Change la position du bras haut en passant par la state machine
     */
    public void setBrasHaut(PositionBras position) {
        try {
            brasHaut.goTo(position);
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }

    public CompletableFuture<Void> setBrasHautAsync(PositionBras position) {
        return CompletableFuture.runAsync(() -> setBrasHaut(position), executor);
    }

    /**
     * Depuis n'importe quelle position, repasse en position de repos
     */
    public void safeHoming() {
        PositionBras currentHaut = brasHaut.current();
        PositionBras currentBas = brasBas.current();

        PositionBras repos = PositionBras.repos(rs.stockTaille());

        boolean brasHautDisableCheck = brasHaut.disableCheck();
        boolean brasBasDisableCheck = brasBas.disableCheck();

        brasHaut.disableCheck(true);
        brasBas.disableCheck(true);

        if (currentHaut.isInside() && currentBas.isInside()) {
            // les deux à l'intérieur
        } else if (currentHaut.isInside()) {
            // le haut à l'intérieur et le bas à l'exterieur
            brasBas.goTo(PositionBras.HORIZONTAL);
            brasHaut.goTo(PositionBras.HORIZONTAL);
            brasBas.goTo(repos);
            brasHaut.goTo(repos);
        } else if (currentBas.isInside()) {
            // le haut à l'extérieur et le bas à l'intérieur
            brasBas.goTo(repos);
            brasHaut.goTo(repos);
        } else {
            // les deux à l'extérieur
            brasHaut.goTo(PositionBras.HORIZONTAL);
            brasBas.goTo(repos);
            brasHaut.goTo(repos);
        }

        brasHaut.disableCheck(brasHautDisableCheck);
        brasBas.disableCheck(brasBasDisableCheck);
    }

    private void setBrasBas(PositionBras state, PointBras pt, TransitionBras transition) {
        for (PointBras point : transition.points()) {
            setBrasBas(point, state, transition.speed());
        }

        setBrasBas(pt, state, transition.speed());
    }

    private void setBrasHaut(PositionBras state, PointBras pt, TransitionBras transition) {
        for (PointBras point : transition.points()) {
            setBrasHaut(point, state, transition.speed());
        }

        setBrasHaut(pt, state, transition.speed());
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
