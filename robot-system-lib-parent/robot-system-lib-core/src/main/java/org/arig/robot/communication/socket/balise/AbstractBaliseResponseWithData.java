package org.arig.robot.communication.socket.balise;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractBaliseResponseWithData<DATA extends Serializable>
    extends AbstractBaliseResponse
    implements Serializable {

  private DATA data;

}
