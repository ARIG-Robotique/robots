package org.arig.robot.communication.socket.ecran;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.arig.robot.communication.socket.AbstractQueryWithDatas;
import org.arig.robot.communication.socket.ecran.enums.EcranAction;

@Data
@EqualsAndHashCode(callSuper = true)
public class UpdatePhotoQuery extends AbstractQueryWithDatas<EcranAction, String> {

    public UpdatePhotoQuery() {
        super(EcranAction.UPDATE_PHOTO);
    }
}
