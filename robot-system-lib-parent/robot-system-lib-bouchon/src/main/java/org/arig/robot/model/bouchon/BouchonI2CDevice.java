package org.arig.robot.model.bouchon;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author gdepuille on 16/10/16.
 */
@Data
@Accessors(fluent = true, chain = true)
public class BouchonI2CDevice {
  private int address;
}
