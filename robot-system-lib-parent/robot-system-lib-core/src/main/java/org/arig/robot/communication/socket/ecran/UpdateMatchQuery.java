package org.arig.robot.communication.socket.ecran;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.arig.robot.communication.socket.AbstractQueryWithDatas;
import org.arig.robot.communication.socket.ecran.enums.EcranAction;
import org.arig.robot.model.ecran.UpdateMatchInfos;

@Data
@EqualsAndHashCode(callSuper = true)
public class UpdateMatchQuery extends AbstractQueryWithDatas<EcranAction, UpdateMatchInfos> {

    public UpdateMatchQuery() {
        super(EcranAction.UPDATE_MATCH);
    }
}
