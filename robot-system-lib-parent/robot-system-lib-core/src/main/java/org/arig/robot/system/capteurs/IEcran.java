package org.arig.robot.system.capteurs;

import org.arig.robot.model.ecran.GetConfigInfos;
import org.arig.robot.model.ecran.UpdateMatchInfos;
import org.arig.robot.model.ecran.UpdatePhotoInfos;
import org.arig.robot.model.ecran.UpdateStateInfos;

public interface IEcran {

    void end();

    GetConfigInfos configInfos();
    void updateState(UpdateStateInfos datas);
    void updateMatch(UpdateMatchInfos datas);
    void updatePhoto(UpdatePhotoInfos photo);

}
