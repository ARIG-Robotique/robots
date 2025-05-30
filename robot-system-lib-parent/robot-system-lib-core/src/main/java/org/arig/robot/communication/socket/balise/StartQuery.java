package org.arig.robot.communication.socket.balise;

import lombok.EqualsAndHashCode;
import org.arig.robot.communication.socket.AbstractQuery;
import org.arig.robot.communication.socket.balise.enums.BaliseAction;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
public class StartQuery
  extends AbstractQuery<BaliseAction>
  implements Serializable {

  public StartQuery() {
    super(BaliseAction.START);
  }

}
