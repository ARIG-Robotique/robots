package org.arig.robot.model.monitor;

import lombok.Data;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * @author gdepuille on 15/11/16.
 */
@Data
public class AbstractMonitor implements Serializable {

  private final Long time = System.currentTimeMillis();
  private final TimeUnit precision = TimeUnit.MILLISECONDS;
}
