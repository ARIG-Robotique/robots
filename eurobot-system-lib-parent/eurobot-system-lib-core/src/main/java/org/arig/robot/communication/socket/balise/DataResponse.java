package org.arig.robot.communication.socket.balise;

import lombok.EqualsAndHashCode;
import org.arig.robot.model.balise.BaliseData;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
public class DataResponse
    extends AbstractBaliseResponseWithData<BaliseData>
    implements Serializable {
}
