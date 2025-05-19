package org.arig.robot.communication.socket.balise;

import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
public class EmptyResponse
  extends AbstractBaliseResponse
  implements Serializable {
}
