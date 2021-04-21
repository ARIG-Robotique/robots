package org.arig.robot.communication.socket.ecran;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.arig.robot.communication.socket.AbstractQueryWithData;
import org.arig.robot.communication.socket.ecran.enums.EcranAction;
import org.arig.robot.model.ecran.UpdateStateInfos;

@Data
@EqualsAndHashCode(callSuper = true)
public class UpdateStateQuery extends AbstractQueryWithData<EcranAction, UpdateStateInfos> {

    public UpdateStateQuery() {
        super(EcranAction.UPDATE_STATE);
    }
}
