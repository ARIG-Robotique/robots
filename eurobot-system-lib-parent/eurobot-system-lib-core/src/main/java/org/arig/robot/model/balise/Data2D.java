package org.arig.robot.model.balise;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import org.arig.robot.communication.socket.balise.AbstractData2D;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Data2D
  extends AbstractData2D
  implements Serializable {
}
