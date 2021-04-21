package org.arig.robot.system.capteurs;

import org.arig.robot.model.ecran.GetConfigInfos;
import org.arig.robot.model.ecran.UpdateMatchInfos;
import org.arig.robot.model.ecran.UpdatePhotoInfos;
import org.arig.robot.model.ecran.UpdateStateInfos;

public interface IEcran {

    void end();

    GetConfigInfos configInfos();
    void updateState(UpdateStateInfos data);
    void updateMatch(UpdateMatchInfos data);
    void updatePhoto(UpdatePhotoInfos photo);

}
