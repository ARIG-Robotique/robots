package org.arig.robot.model.balise;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import org.arig.robot.communication.socket.balise.AbstractDataResponseData;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaliseData
  extends AbstractDataResponseData<Data2D, Data3D>
  implements Serializable {

  public BaliseData() {
    super();
  }

  public BaliseData(List<Data2D> data2D, List<Data3D> data3D) {
    super(data2D, data3D);
  }

}
