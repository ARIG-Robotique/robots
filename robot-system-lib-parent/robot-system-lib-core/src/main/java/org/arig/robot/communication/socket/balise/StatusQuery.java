package org.arig.robot.communication.socket.balise;

import lombok.EqualsAndHashCode;
import org.arig.robot.communication.socket.AbstractQuery;
import org.arig.robot.communication.socket.balise.enums.BaliseAction;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
public class StatusQuery
  extends AbstractQuery<BaliseAction>
  implements Serializable {

  public StatusQuery() {
    super(BaliseAction.STATUS);
  }

}
