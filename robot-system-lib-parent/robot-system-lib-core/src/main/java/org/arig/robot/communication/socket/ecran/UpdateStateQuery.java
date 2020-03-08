package org.arig.robot.communication.socket.ecran;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.arig.robot.communication.socket.AbstractQueryWithDatas;
import org.arig.robot.communication.socket.ecran.enums.EcranAction;
import org.arig.robot.model.ecran.UpdateStateInfos;

@Data
@EqualsAndHashCode(callSuper = true)
public class UpdateStateQuery extends AbstractQueryWithDatas<EcranAction, UpdateStateInfos> {

    public UpdateStateQuery() {
        super(EcranAction.UPDATE_STATE);
    }
}
