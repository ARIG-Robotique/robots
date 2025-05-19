package org.arig.robot.system.avoiding;

public interface AvoidingService {

  enum Mode {
    BASIC, FULL, BASIC_RETRY, SEMI_COMPLETE
  }

  /**
   * Execution du système d'évittement
   */
  void process();

  void setSafeAvoidance(boolean enabled);

}
