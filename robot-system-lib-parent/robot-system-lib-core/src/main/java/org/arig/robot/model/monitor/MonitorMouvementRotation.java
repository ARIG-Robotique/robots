package org.arig.robot.model.monitor;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.arig.robot.model.enums.TypeMouvement;

/**
 * @author gdepuille on 15/11/16.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MonitorMouvementRotation extends AbstractMonitorMouvement {

  private final TypeMouvement type = TypeMouvement.ROTATION;

  private Double angle;
  private Double fromAngle;
  private Double toAngle;
}
