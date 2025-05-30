package org.arig.robot.communication.socket.balise;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class AbstractData3D<
  NAME extends Enum<NAME>,
  TYPE extends Enum<TYPE>
  > implements Serializable {

  protected NAME name;
  protected TYPE type;
  private int r;
  private int x;
  private int y;
  private long age;
  private DataResponseMetadata metadata;

}
