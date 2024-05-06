package org.arig.robot.communication.socket.balise;

import lombok.EqualsAndHashCode;
import org.arig.robot.communication.socket.AbstractQueryWithData;
import org.arig.robot.communication.socket.balise.enums.BaliseAction;

@EqualsAndHashCode(callSuper = true)
public class ConfigQuery extends AbstractQueryWithData<BaliseAction, ConfigQueryData> {

  public ConfigQuery() {
    super(BaliseAction.CONFIG);
  }

  public ConfigQuery(ConfigQueryData data) {
    super(BaliseAction.CONFIG, data);
    }

}
