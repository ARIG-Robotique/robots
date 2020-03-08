package org.arig.robot.communication.socket.ecran;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.arig.robot.communication.socket.AbstractQuery;
import org.arig.robot.communication.socket.ecran.enums.EcranAction;

@Data
@EqualsAndHashCode(callSuper = true)
public class GetConfigQuery extends AbstractQuery<EcranAction> {

    public GetConfigQuery() {
        super(EcranAction.GET_CONFIG);
    }
}
