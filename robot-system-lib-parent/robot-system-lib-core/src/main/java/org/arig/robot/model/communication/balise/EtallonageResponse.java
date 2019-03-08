package org.arig.robot.model.communication.balise;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.arig.robot.model.communication.AbstractResponse;
import org.arig.robot.model.communication.balise.enums.BaliseAction;

@Data
@EqualsAndHashCode(callSuper = true)
public class EtallonageResponse extends AbstractResponse<BaliseAction> {
}
