package org.arig.robot.communication.socket.balise;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.arig.robot.communication.socket.AbstractResponse;
import org.arig.robot.communication.socket.balise.enums.BaliseAction;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractBaliseResponse
    extends AbstractResponse<BaliseAction>
    implements Serializable {

  private int index;

}
