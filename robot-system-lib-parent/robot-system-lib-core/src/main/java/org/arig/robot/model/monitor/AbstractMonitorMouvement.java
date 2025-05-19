package org.arig.robot.model.monitor;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.arig.robot.model.enums.TypeMouvement;

/**
 * @author gdepuille on 15/11/16.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractMonitorMouvement extends AbstractMonitor {

  public abstract TypeMouvement getType();
}
