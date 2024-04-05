package org.arig.robot.system.capteurs.socket;

import org.arig.robot.model.ecran.AbstractEcranConfig;
import org.arig.robot.model.ecran.AbstractEcranState;
import org.arig.robot.model.ecran.EcranMatchInfo;
import org.arig.robot.model.ecran.EcranParams;
import org.arig.robot.model.ecran.EcranPhoto;

public interface IEcran<CONFIG extends AbstractEcranConfig, STATE extends AbstractEcranState> {

    void end();

    boolean setParams(EcranParams params);
    CONFIG configInfos();
    void updateState(STATE data);
    void updateMatch(EcranMatchInfo data);
    void updatePhoto(EcranPhoto photo);

}
