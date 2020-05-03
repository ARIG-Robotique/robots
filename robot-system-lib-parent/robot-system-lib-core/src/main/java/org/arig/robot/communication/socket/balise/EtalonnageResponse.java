package org.arig.robot.communication.socket.balise;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.arig.robot.communication.socket.AbstractResponse;
import org.arig.robot.communication.socket.AbstractResponseWithDatas;
import org.arig.robot.communication.socket.balise.enums.BaliseAction;
import org.arig.robot.model.balise.EtalonnageBalise;

@Data
@EqualsAndHashCode(callSuper = true)
public class EtalonnageResponse extends AbstractResponseWithDatas<BaliseAction, EtalonnageBalise> {
}
