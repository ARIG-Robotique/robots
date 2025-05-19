package org.arig.robot.communication.socket.ecran;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.arig.robot.communication.socket.AbstractQuery;
import org.arig.robot.communication.socket.ecran.enums.EcranAction;

@Data
@EqualsAndHashCode(callSuper = true)
public class ExitQuery extends AbstractQuery<EcranAction> {

  public ExitQuery() {
    super(EcranAction.EXIT);
  }

}
