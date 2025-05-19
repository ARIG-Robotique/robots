package org.arig.robot.communication.socket.balise;

import lombok.EqualsAndHashCode;
import org.arig.robot.communication.socket.AbstractQueryWithData;
import org.arig.robot.communication.socket.balise.enums.BaliseAction;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
public class ZoneQuery
  extends AbstractQueryWithData<BaliseAction, ZoneQueryData>
  implements Serializable {

  public ZoneQuery() {
    super(BaliseAction.ZONE);
  }

  public ZoneQuery(ZoneQueryData data) {
    super(BaliseAction.ZONE, data);
  }

}
