package org.arig.robot.services;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.bras.AnglesBras;
import org.arig.robot.model.bras.ConfigBras;
import org.arig.robot.model.bras.CurrentBras;
import org.arig.robot.model.bras.PointBras;
import org.arig.robot.model.bras.PositionBras;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Accessors(fluent = true)
public class BrasInstance {

    // position du pivot de l'epaule (origine = sol au milieu du robot)
    public static final int X = 105;
    public static final int Y = 261;
    // rayon de chaque segment
    public static final int R1 = 72;
    public static final int R2 = 72;
    public static final int R3 = 110;
    // angles limites
    public static final int A1_MIN = -158;
    public static final int A1_MAX = 20;
    public static final int A2_MIN = -52;
    public static final int A2_MAX = 135;
    public static final int A3_MIN = -127;
    public static final int A3_MAX = 33;
    // angles initiaux
    public static final int A2_INIT = A2_MAX;
    public static final int A1_INIT = A1_MIN;
    public static final int A3_INIT = -66;

    private static final int MAX_SPEED = 100;

    // positions caractéristiques
    public static final int PRISE_PLANTE_SOL_Y = 40;
    public static final int PRISE_POT_SOL_Y = 40;
    public static final int PRISE_POT_POT_Y = PRISE_POT_SOL_Y + 10;
    public static final int SORTIE_POT_POT_Y = 120;
    public static final int DEPOSE_SOL_Y = 60;

    @FunctionalInterface
    public interface ServoCallback {
        void accept(double a1, double a2, double a3, int speed, boolean wait);
    }

    private final String name;
    private final ServoCallback servoCallback;

    @Getter
    private final ConfigBras config;

    @Getter
    private CurrentBras current;

    private final Map<PositionBras, PointBras> positions = new HashMap<>();

    public Set<PositionBras> states() {
        return positions.keySet();
    }

    public BrasInstance(String name, boolean back, ServoCallback servoCallback) {
        this.name = name;
        this.servoCallback = servoCallback;
        this.config = new ConfigBras(back, X, Y, R1, R2, R3, A1_MIN, A1_MAX, A2_MIN, A2_MAX, A3_MIN, A3_MAX, true);

        PointBras initstate = new PointBras(105, 95, -90, true); // dois matcher les angles init
        current = new CurrentBras(new AnglesBras(A1_INIT, A2_INIT, A3_INIT), initstate);

        positions.put(PositionBras.INIT, initstate);
        positions.put(PositionBras.HORIZONTAL, new PointBras(X + R1 + R2 + R3, Y, 0, true));
        positions.put(PositionBras.TRANSPORT, new PointBras(155, 95, -90, true)); // position quand on transporte un truc
        positions.put(PositionBras.CALLAGE_PANNEAUX, new PointBras(215, 205, 0, true)); // position d'init avec la pince à l'horizontale
    }

    public boolean setByName(PositionBras positionBras, int speed, boolean wait) {
        assert positions.containsKey(positionBras);
        return set(positions.get(positionBras), speed, wait);
    }

    public boolean set(PointBras pt, int speed, boolean wait) {
        pt = resolvePoint(pt);

        if (pt.invertA1 == null) {
            pt.invertA1 = config.preferA1Min;
        }

        if (speed == 0) {
            speed = MAX_SPEED;
        }

        log.info("Bras {} x={} y={} a={} invertA1={}", name, pt.x, pt.y, pt.a, pt.invertA1);
        AnglesBras angles = calculerAngles(pt.x, pt.y, pt.a, pt.invertA1, true);

        if (angles == null || angles.isError()) {
            return false;
        }

        log.info("Bras {} a1={} a2={} a3={}", name, angles.a1, angles.a2, angles.a3);
        servoCallback.accept(angles.a1, angles.a2, angles.a3, speed, wait);
        current = new CurrentBras(angles, pt);

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

    private PointBras resolvePoint(PointBras pt) {
        if (pt instanceof PointBras.PointBrasTranslated) {
            return new PointBras(
                    pt.x + current.x,
                    pt.y + current.y,
                    current.a,
                    current.invertA1
            );
        } else if (pt instanceof PointBras.PointBrasWithX) {
            return new PointBras(
                    pt.x,
                    current.y,
                    current.a,
                    current.invertA1
            );
        } else if (pt instanceof PointBras.PointBrasWithY) {
            return new PointBras(
                    current.x,
                    pt.y,
                    current.a,
                    current.invertA1
            );
        } else if (pt instanceof PointBras.PointBrasWithAngle) {
            return new PointBras(
                    current.x,
                    current.y,
                    pt.a,
                    current.invertA1
            );
        } else if (pt instanceof PointBras.PointBrasRotated) {
            // position du doigt dans le réferentiel du poignet
            double currentA = Math.toRadians(current.a);
            double xOrig = Math.cos(currentA) * config.r3;
            double yOrig = Math.sin(currentA) * config.r3;

            // rotation du doigt
            double toRotate = Math.toRadians(pt.a);
            double xNew = xOrig * Math.cos(toRotate) - yOrig * Math.sin(toRotate);
            double yNew = xOrig * Math.sin(toRotate) + yOrig * Math.cos(toRotate);

            // remise dans le referentiel global
            return new PointBras(
                    (int) Math.round(current.x - xOrig + xNew),
                    (int) Math.round(current.y - yOrig + yNew),
                    pt.a + current.a,
                    current.invertA1
            );
        } else {
            return pt;
        }
    }

}
