package org.arig.robot.communication.socket.balise;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.arig.robot.communication.socket.AbstractResponseWithDatas;
import org.arig.robot.communication.socket.balise.enums.BaliseAction;

@Data
@EqualsAndHashCode(callSuper = true)
public class PhotoResponse extends AbstractResponseWithDatas<BaliseAction, String> {
}
