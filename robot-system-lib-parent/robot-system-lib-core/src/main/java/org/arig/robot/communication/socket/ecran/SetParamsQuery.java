package org.arig.robot.communication.socket.ecran;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.arig.robot.communication.socket.AbstractQueryWithData;
import org.arig.robot.communication.socket.ecran.enums.EcranAction;
import org.arig.robot.model.ecran.EcranParams;

@Data
@EqualsAndHashCode(callSuper = true)
public class SetParamsQuery extends AbstractQueryWithData<EcranAction, EcranParams> {
    public SetParamsQuery() {
        super(EcranAction.SET_PARAMS);
    }
}
