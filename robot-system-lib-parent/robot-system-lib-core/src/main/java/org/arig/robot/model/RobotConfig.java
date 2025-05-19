package org.arig.robot.model;

import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true, chain = true)
public class RobotConfig {

  /**
   * scheduler
   */
  @Setter(lombok.AccessLevel.NONE)
  private double calageGlobalTimeMs;
  @Setter(lombok.AccessLevel.NONE)
  private double calageCourtMs;

  public RobotConfig calageTimeMs(double calageGlobalTimeMs, double calageCourtMs) {
    this.calageGlobalTimeMs = calageGlobalTimeMs;
    this.calageCourtMs = calageCourtMs;
    return this;
  }

  public double calageTimeMs(boolean court) {
    return court ? calageCourtMs : calageGlobalTimeMs;
  }

  private double asservTimeMs;
  private int i2cReadTimeMs;
  private double sampleTimeS;

  /**
   * pathfinding
   */
  private int pathFindingTailleObstacle;
  private int pathFindingTailleObstacleArig;
  private int lidarOffsetPointMm;
  private int lidarClusterSizeMm;
  private int avoidanceWaitTimeMs;
  private int avoidanceWaitTimeLongMs;
  private int pathFindingSeuilProximite;
  private int pathFindingSeuilProximiteSafe;
  private int pathFindingSeuilProximiteArig;
  private int pathFindingAngle;
  private int pathFindingAngleSafe;

  /**
   * Seuils tensions
   */
  double seuilTensionMoteurs;
  double seuilTensionServos;

  /**
   * Mécanique
   */
  double distanceCalageCote;
  double distanceCalageAvant;
  double distanceCalageArriere;

  /**
   * mouvement
   */
  private long vitesseMax;
  private long vitesseMin;
  private int vitesseDefRatio;

  private double rampeAccelDistance;
  private double rampeDecelDistance;

  private long vitesseOrientationMax;
  private long vitesseOrientationMin;
  private int vitesseOrientationDefRatio;

  private double rampeAccelOrientation;
  private double rampeDecelOrientation;

  public RobotConfig vitesse(long min, long max, int defRatio) {
    this.vitesseMin = min;
    this.vitesseMax = max;
    this.vitesseDefRatio = defRatio;
    return this;
  }

  public RobotConfig rampeDistance(double accel, double decel) {
    rampeAccelDistance = accel;
    rampeDecelDistance = decel;
    return this;
  }

  public long vitesse() {
    return vitesse(vitesseDefRatio);
  }

  /**
   * Pour un nombre entre 0 et 100 retourne une vitesse proportionnelle entre min et max
   *
   * @param percent Pourcentage de la vitesse
   */
  public long vitesse(int percent) {
    double pct = (double) percent / 100;
    return (long) (((vitesseMax - vitesseMin) * pct) + vitesseMin);
  }

  public long rampeAccelDistance() {
    return rampeAccelDistance(100);
  }

  public long rampeAccelDistance(int percent) {
    double pct = (double) percent / 100;
    return (long) (rampeAccelDistance * pct);
  }

  public long rampeDecelDistance() {
    return rampeDecelDistance(100);
  }

  public long rampeDecelDistance(int percent) {
    double pct = (double) percent / 100;
    return (long) (rampeDecelDistance * pct);
  }

  public RobotConfig vitesseOrientation(long min, long max, int defRatio) {
    this.vitesseOrientationMin = min;
    this.vitesseOrientationMax = max;
    this.vitesseOrientationDefRatio = defRatio;
    return this;
  }

  public RobotConfig rampeOrientation(double accel, double decel) {
    rampeAccelOrientation = accel;
    rampeDecelOrientation = decel;
    return this;
  }

  public long vitesseOrientation() {
    return vitesseOrientation(vitesseOrientationDefRatio);
  }

  /**
   * Pour un nombre en 0 et 100 retourne une vitesse proportionnelle entre min et max
   *
   * @param percent Pourcentage de la vitesse
   */
  public long vitesseOrientation(int percent) {
    double pct = (double) percent / 100;
    return (long) (((vitesseOrientationMax - vitesseOrientationMin) * pct) + vitesseOrientationMin);
  }

  public double rampeAccelOrientation() {
    return rampeAccelOrientation(100);
  }

  public double rampeAccelOrientation(int percent) {
    double pct = (double) percent / 100;
    return rampeAccelOrientation * pct;
  }

  public double rampeDecelOrientation() {
    return rampeDecelOrientation(100);
  }

  public double rampeDecelOrientation(int percent) {
    double pct = (double) percent / 100;
    return rampeDecelOrientation * pct;
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

  /**
   * Autres
   */
  private int waitLed;
  private int timeoutPompe;
  private int timeoutColor;
}
