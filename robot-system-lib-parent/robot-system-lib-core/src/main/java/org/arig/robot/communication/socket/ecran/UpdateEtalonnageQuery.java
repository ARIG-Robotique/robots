package org.arig.robot.communication.socket.ecran;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.arig.robot.communication.socket.AbstractQueryWithDatas;
import org.arig.robot.communication.socket.ecran.enums.EcranAction;
import org.arig.robot.model.ecran.UpdateEtalonnageData;

@Data
@EqualsAndHashCode(callSuper = true)
public class UpdateEtalonnageQuery extends AbstractQueryWithDatas<EcranAction, UpdateEtalonnageData> {

    public UpdateEtalonnageQuery() {
        super(EcranAction.UPDATE_ETALONNAGE);
    }
}
