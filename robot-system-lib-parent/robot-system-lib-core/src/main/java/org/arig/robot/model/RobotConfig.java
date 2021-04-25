package org.arig.robot.model;

import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Map;

@Data
@Accessors(fluent = true, chain = true)
public class RobotConfig {

    /**
     * scheduler
     */
    private double calageTimeMs;
    private double asservTimeMs;
    private double i2cReadTimeMs;
    private double sampleTimeS;

    /**
     * pathfinding
     */
    private int pathFindingTailleObstacle;
    private int lidarOffsetPointMm;
    private int lidarClusterSizeMm;
    private int avoidanceWaitTimeMs;
    private int pathFindingSeuilProximite;
    private int pathFindingSeuilProximiteSafe;
    private int pathFindingAngle;
    private int pathFindingAngleSafe;

    /**
     * servos
     */
    double seuilAlimentationServos;
    // Map<IdServo, Triple<PosMin, Temps, PosMax>>
    Map<Byte, Triple<Integer, Integer, Integer>> servosMinTimeMax;
    // Map<IdBatch, Map<BatchPos, [IdServo, Pos]>>
    Map<Byte, Map<Byte, int[][]>> servosBatch;

    /**
     * mouvement
     */
    private long vitesseMax;
    private long vitesseMin;
    private int vitesseDefRatio;
    private long vitesseOrientationMax;
    private long vitesseOrientationMin;
    private int vitesseOrientationDefRatio;

    public RobotConfig vitesse(long min, long max, int defRatio) {
        this.vitesseMin = min;
        this.vitesseMax = max;
        this.vitesseDefRatio = defRatio;
        return this;
    }

    public long vitesse() {
        return vitesse(vitesseDefRatio);
    }

    /**
     * Pour un nombre en 1 et 10 retourne une vitesse entre min et max
     */
    public long vitesse(int ratio) {
        ratio = Math.min(10, Math.max(1, ratio)) - 1;
        return ratio / 9 * (vitesseMax - vitesseMin) + vitesseMin;
    }

    public RobotConfig vitesseOrientation(long min, long max, int defRatio) {
        this.vitesseOrientationMin = min;
        this.vitesseOrientationMax = max;
        this.vitesseOrientationDefRatio = defRatio;
        return this;
    }

    public long vitesseOrientation() {
        return vitesseOrientation(vitesseOrientationDefRatio);
    }

    /**
     * Pour un nombre en 1 et 10 retourne une vitesse entre min et max
     */
    public long vitesseOrientation(int ratio) {
        ratio = Math.min(10, Math.max(1, ratio)) - 1;
        return ratio / 9 * (vitesseOrientationMax - vitesseOrientationMin) + vitesseOrientationMin;
    }

    /* Fenetre d'arret / approche distance (en pulse) */
    private double fenetreApprocheSansFreinDistance;
    private double fenetreApprocheAvecFreinDistance;
    private double fenetreArretDistance;

    /* Fenetre d'arret / approche orientation (en pulse) */
    private double fenetreApprocheSansFreinOrientation;
    private double fenetreApprocheAvecFreinOrientation;
    private double fenetreArretOrientation;

    /* Angle de départ pour les déplacements (en pulse).
     * Si l'angle est supérieur en absolu, on annule la distance
     * afin de naviguer en priorité en marche avant.
     * Cela a pour effet de tourner sur place en reculant avant de partir en avant.
     */
    private double startAngleDemiTour;

    /* Angle a partir duquel la vitesse de déplacement devient null (en pulse)
     * Si l'angle du point suivant est inférieur un coef visant a limiter la vitesse est calculé
     */
    private double startAngleLimitSpeedDistance;
}