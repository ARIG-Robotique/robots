package org.arig.robot.model.communication.balise;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.arig.robot.model.communication.AbstractResponseWithDatas;
import org.arig.robot.model.communication.balise.enums.BaliseAction;

@Data
@EqualsAndHashCode(callSuper = true)
public class PhotoResponse extends AbstractResponseWithDatas<BaliseAction, String> {
}
