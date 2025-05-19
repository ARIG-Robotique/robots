package org.arig.robot.system.motors;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class AbstractMotors.
 *
 * @author gdepuille
 */
@Slf4j
public abstract class AbstractMotor {

  protected final int offsetValue;
  protected int minVal;
  protected int maxVal;
  protected int prev;

  @Getter
  @Setter
  @Accessors(fluent = true)
  private boolean reverse = false;

  public AbstractMotor(int offsetValue) {
    this.offsetValue = offsetValue;
  }

  /**
   * Méthode d'arrêt du moteur
   */
  public void stop() {
    speed(getStopSpeed());
  }

  /**
   * Récupération de la valeur pour le stop.
   */
  public int getStopSpeed() {
    return 0;
  }

  /**
   * Valeur minimal pour la vitesse du moteur.
   */
  public final int getMinSpeed() {
    return minVal - offsetValue;
  }

  /**
   * Valeur maximal pour la vitesse du moteur
   */
  public final int getMaxSpeed() {
    return maxVal - offsetValue;
  }

  /**
   * Vitesse courante du moteur
   */
  public final int currentSpeed() {
    return prev - offsetValue;
  }

  public abstract void init();

  public abstract void speed(final int val);

  public abstract void printVersion();

  /**
   * Méthode de contrôle du bornage des commandes moteurs.
   *
   * @param val the val
   * @return the int
   */
  protected int check(final int val) {
    int result = val;
    if (val < minVal) {
      result = minVal;
    }
    if (val > maxVal) {
      result = maxVal;
    }

    return result;
  }
}
