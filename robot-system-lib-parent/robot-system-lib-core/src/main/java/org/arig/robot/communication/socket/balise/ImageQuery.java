package org.arig.robot.communication.socket.balise;

import lombok.EqualsAndHashCode;
import org.arig.robot.communication.socket.AbstractQueryWithData;
import org.arig.robot.communication.socket.balise.enums.BaliseAction;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
public class ImageQuery
  extends AbstractQueryWithData<BaliseAction, ImageQueryData>
  implements Serializable {

  public ImageQuery() {
    super(BaliseAction.IMAGE);
  }

  public ImageQuery(ImageQueryData data) {
    super(BaliseAction.IMAGE, data);
  }

}
