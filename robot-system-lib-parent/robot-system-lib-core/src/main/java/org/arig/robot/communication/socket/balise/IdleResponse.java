package org.arig.robot.communication.socket.balise;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
public class IdleResponse
  extends AbstractBaliseResponse
  implements Serializable {
}
