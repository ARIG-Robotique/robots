package org.arig.robot.communication.socket.balise;

import lombok.EqualsAndHashCode;
import org.arig.robot.communication.socket.AbstractQueryWithData;
import org.arig.robot.communication.socket.balise.enums.BaliseAction;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
public class IdleQuery
  extends AbstractQueryWithData<BaliseAction, IdleQueryData>
  implements Serializable {

  public IdleQuery() {
    super(BaliseAction.IDLE);
  }

  public IdleQuery(IdleQueryData data) {
    super(BaliseAction.IDLE, data);
  }

}
