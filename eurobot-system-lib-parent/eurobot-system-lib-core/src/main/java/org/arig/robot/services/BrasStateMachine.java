package org.arig.robot.services;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.bras.AnglesBras;
import org.arig.robot.model.bras.ConfigBras;
import org.arig.robot.model.bras.CurrentBras;
import org.arig.robot.model.bras.OptionBras;
import org.arig.robot.model.bras.PointBras;
import org.arig.robot.model.bras.PositionBras;
import org.arig.robot.model.bras.TransitionBras;
import org.arig.robot.utils.StateMachine;

@Slf4j
@Accessors(fluent = true)
public class BrasStateMachine extends StateMachine<PositionBras, PointBras, TransitionBras, OptionBras> {

    public static final int X = 105;
    public static final int Y = 261;
    public static final int R1 = 72;
    public static final int R2 = 72;
    public static final int R3 = 110;
    public static final int A1_MIN = -158;
    public static final int A1_MAX = 19;
    public static final int A1_INIT = A1_MIN;
    public static final int A2_MIN = -44;
    public static final int A2_MAX = 135;
    public static final int A2_INIT = A2_MAX;
    public static final int A3_MIN = -127;
    public static final int A3_MAX = 33;
    public static final int A3_INIT = -66;

    private final int MAX_SPEED = 100;
    private final int SLOW_SPEED = 50;

    @Getter
    private final ConfigBras config;
    private final ServoCallback servoCallback;

    @Getter
    private CurrentBras current;

    @FunctionalInterface
    public interface ServoCallback {
        void accept(double a1, double a2, double a3, int speed);
    }

    public BrasStateMachine(String name, boolean back, ServoCallback servo) {
        super(name);

        this.config = new ConfigBras(back, X, Y, R1, R2, R3, A1_MIN, A1_MAX, A2_MIN, A2_MAX, A3_MIN, A3_MAX, true);
        this.servoCallback = servo;

        disableCheck(true);

        /* INIT */
        defaultTransition(new TransitionBras(MAX_SPEED, new PointBras[0]));
        currentState(PositionBras.INIT);

        onState((state, pt, transition, opt) -> {
            for (PointBras point : transition.points()) {
                set(point, state, opt == OptionBras.SLOW ? SLOW_SPEED : MAX_SPEED);
            }

            set(pt, state, opt == OptionBras.SLOW ? SLOW_SPEED : MAX_SPEED);
        });

        PointBras initstate = new PointBras(105, 95, -90, true); // dois matcher les angles init

        state(PositionBras.INIT, initstate);
        state(PositionBras.HORIZONTAL, new PointBras(X + R1 + R2 + R3, Y, 0, true));

        current = new CurrentBras(PositionBras.INIT, new AnglesBras(A1_INIT, A2_INIT, A3_INIT), initstate);

        /* STATES */

        final int PRISE_PLANTE_SOL_Y = 55;
        final int PRISE_POT_SOL_Y = 60;
        final int DEPOSE_SOL_Y = PRISE_POT_SOL_Y + 10;

        final int WORK_X = 220;

        state(PositionBras.PRISE_PLANTE_AVANT, new PointBras(110, PRISE_PLANTE_SOL_Y, -90, true));
        state(PositionBras.PRISE_POT, new PointBras(140, PRISE_POT_SOL_Y, -90, true));
        state(PositionBras.PRISE_POT_POT, new PointBras(WORK_X, PRISE_POT_SOL_Y + 15, -90, true));
        state(PositionBras.SORTIE_POT_POT, new PointBras(WORK_X, 145, -90, true));
        state(PositionBras.DEPOSE_PLANTE_POT, new PointBras(WORK_X, 120, -90, true));
        state(PositionBras.DEPOSE_SOL, new PointBras(WORK_X, DEPOSE_SOL_Y, -90, true));
        state(PositionBras.DEPOSE_JARDINIERE, new PointBras(250, 150, -90, true));

        build();
    }

    /**
     * Change la position du bras en direct sans passer par la state machine
     */
    public boolean set(PointBras pt, PositionBras state, int speed) {
        log.debug("Bras {} x={} y={} a={} invertA1={}", name, pt.x, pt.y, pt.a, pt.invertA1);
        AnglesBras angles = calculerAngles(pt.x, pt.y, pt.a, pt.invertA1, true);

        if (angles == null || angles.isError()) {
            return false;
        }

        if (state == null) {
            currentState(null);
        }

        log.debug("Bras {} a1={} a2={} a3={}", name, angles.a1, angles.a2, angles.a3);
        servoCallback.accept(angles.a1, angles.a2, angles.a3, speed);
        current = new CurrentBras(state, angles, pt);

        return true;
    }

    public AnglesBras calculerAngles(int x, int y, int a, Boolean invertA1, boolean enableLog) {
        if (invertA1 == null) {
            invertA1 = config.preferA1Min;
        }

        double a3Absolute = Math.toRadians(a);

        // Calcul de l'axe du servo moteur 3
        double xTemp = x - Math.cos(a3Absolute) * config.r3;
        double yTemp = y - Math.sin(a3Absolute) * config.r3;

        // Calcul de r
        double dX = xTemp - config.x;
        double dY = yTemp - config.y;
        double r = Math.sqrt(Math.pow(dX, 2) + Math.pow(dY, 2));

        if (r > config.r1 + config.r2) {
            if (enableLog) {
                log.warn("Impossible d'atteindre le point: r={} r1+r2={}", r, config.r1 + config.r2);
            }
            return null;
        }

        // Calcul de alpha1 (angle du servo moteur 1)
        double alpha3 = Math.atan2(dY, dX);
        double alpha4 = alKashiAngleRad(config.r1, r, config.r2);
        double alpha1 = alpha4 + alpha3;

        // Calcul de alpha2 (angle du servo moteur 2)
        double alpha6 = alKashiAngleRad(config.r2, config.r1, r);
        double alpha2 = alpha6 - Math.PI;

        // symétrise alpha1 et alpha2
        if (invertA1) {
            alpha1 -= (alpha1 - alpha3) * 2;
            alpha2 *= -1;
        }

        // Calcul de alpha3 (angle du servo moteur 3)
        double a3 = Math.toDegrees(a3Absolute - (alpha1 + alpha2));
        if (a3 < config.a3Min && a3 + 360 <= config.a3Max) {
            a3 += 360;
        }
        if (a3 > config.a3Max && a3 - 360 >= config.a3Min) {
            a3 -= 360;
        }

        AnglesBras result = new AnglesBras(
                Math.toDegrees(alpha1),
                Math.toDegrees(alpha2),
                a3
        );

        result.a1Error = result.a1 < config.a1Min || result.a1 > config.a1Max;
        result.a2Error = result.a2 < config.a2Min || result.a2 > config.a2Max;
        result.a3Error = result.a3 < config.a3Min || result.a3 > config.a3Max;

        // si l'inversion entraine une erreur, on essaye sans inversion
//        if (first && result.isError()) {
//            AnglesBras newResult = calculerAngles(configBras, x, y, a, false, !preferA1Min);
//            if (newResult != null && !newResult.isError()) {
//                return newResult;
//            }
//        }

        if (enableLog) {
            if (result.a1Error) {
                log.warn("Impossible d'atteindre le point: a1={} a1Min={} a1Max={}", result.a1, config.a1Min, config.a1Max);
            }
            if (result.a2Error) {
                log.warn("Impossible d'atteindre le point: a2={} a2Min={} a2Max={}", result.a2, config.a2Min, config.a2Max);
            }
            if (result.a3Error) {
                log.warn("Impossible d'atteindre le point: a3={} a3Min={} a3Max={}", result.a3, config.a3Min, config.a3Max);
            }
        }

        return result;
    }

    private double alKashiAngleRad(double a, double b, double c) {
        return Math.acos((Math.pow(a, 2) + Math.pow(b, 2) - Math.pow(c, 2)) / (2 * a * b));
    }

}
