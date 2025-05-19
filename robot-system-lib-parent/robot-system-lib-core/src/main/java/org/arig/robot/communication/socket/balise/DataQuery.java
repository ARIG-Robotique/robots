package org.arig.robot.communication.socket.balise;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import org.arig.robot.communication.socket.AbstractQueryWithData;
import org.arig.robot.communication.socket.balise.enums.BaliseAction;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataQuery
  extends AbstractQueryWithData<BaliseAction, DataQueryData<? extends Enum<?>>>
  implements Serializable {

  public DataQuery() {
    super(BaliseAction.DATA);
  }

  public DataQuery(DataQueryData<?> data) {
    super(BaliseAction.DATA, data);
  }

}
