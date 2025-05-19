package org.arig.robot.system.motion;

/**
 * The Interface IAsservissement.
 *
 * @author gdepuille
 */
public interface IAsservissement {

  /**
   * Process.
   *
   * @param timeStepMs
   * @param obstacleDetected
   */
  void process(long timeStepMs, boolean obstacleDetected);

  /**
   * Reset.
   */
  default void reset() {
    reset(false);
  }

  /**
   * Reset.
   *
   * @param resetFilters the reset filters
   */
  void reset(final boolean resetFilters);
}
