package org.arig.robot.communication.socket.balise;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.arig.robot.communication.socket.AbstractResponseWithDatas;
import org.arig.robot.communication.socket.balise.enums.BaliseAction;
import org.arig.robot.model.balise.StatutBalise;

@Data
@EqualsAndHashCode(callSuper = true)
public class StatusResponse extends AbstractResponseWithDatas<BaliseAction, StatutBalise> {
}
