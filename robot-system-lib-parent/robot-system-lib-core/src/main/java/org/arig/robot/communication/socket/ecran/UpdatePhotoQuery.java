package org.arig.robot.communication.socket.ecran;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.arig.robot.communication.socket.AbstractQueryWithData;
import org.arig.robot.communication.socket.ecran.enums.EcranAction;
import org.arig.robot.model.ecran.UpdatePhotoInfos;

@Data
@EqualsAndHashCode(callSuper = true)
public class UpdatePhotoQuery extends AbstractQueryWithData<EcranAction, UpdatePhotoInfos> {

    public UpdatePhotoQuery() {
        super(EcranAction.UPDATE_PHOTO);
    }
}
