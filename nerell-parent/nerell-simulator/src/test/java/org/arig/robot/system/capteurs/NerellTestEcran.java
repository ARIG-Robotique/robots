package org.arig.robot.system.capteurs;

import lombok.Getter;
import org.arig.robot.model.ecran.GetConfigInfos;
import org.arig.robot.model.ecran.UpdateMatchInfos;
import org.arig.robot.model.ecran.UpdatePhotoInfos;
import org.arig.robot.model.ecran.UpdateStateInfos;

public class NerellTestEcran implements IEcran {

    @Getter
    private final GetConfigInfos configInfos;

    public NerellTestEcran() {
         configInfos = new GetConfigInfos();
         configInfos.setTeam(-1);
    }

    @Override
    public GetConfigInfos configInfos() {
        return configInfos;
    }

    @Override
    public void end() {
    }

    @Override
    public void updateState(final UpdateStateInfos data) {
    }

    @Override
    public void updateMatch(final UpdateMatchInfos data) {
    }

    @Override
    public void updatePhoto(final UpdatePhotoInfos photo) {
    }
}
