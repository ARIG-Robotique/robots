package org.arig.robot.model;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import org.arig.robot.constants.ConstantesConfig;

/**
 * @author gdepuille on 13/10/16.
 */
@Data
@Builder
@Accessors(fluent = true, chain = true)
public class RobotName {

  public enum RobotIdentification {
    NERELL, ODIN, TINKER, PAMI_TRIANGLE, PAMI_CARRE, PAMI_ROND
  }

  private String name;
  private final RobotIdentification id;
  private final String version;

  public static final RobotIdentification fromPamiID() {
    switch (System.getProperty(ConstantesConfig.keyPamiId)) {
      case "triangle":
        return RobotIdentification.PAMI_TRIANGLE;
      case "carre":
        return RobotIdentification.PAMI_CARRE;
      case "rond":
        return RobotIdentification.PAMI_ROND;
      default:
        return null;
    }
  }
}
