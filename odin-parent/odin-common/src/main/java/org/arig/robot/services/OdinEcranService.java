package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.EurobotStatus;
import org.arig.robot.model.ecran.UpdateStateInfos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OdinEcranService extends AbstractEcranService {

    @Autowired
    private EurobotStatus rs;

    @Override
    public void updateStateInfo(UpdateStateInfos stateInfos) {
        super.updateStateInfo(stateInfos);
        if (stateInfos.isOtherRobot()) {
            stateInfos.setTeam(rs.team().ordinal());
            stateInfos.setStrategy(rs.strategy().ordinal());
            stateInfos.setDoubleDepose(rs.doubleDepose());
            stateInfos.setDeposePartielle(rs.deposePartielle());
            stateInfos.setEchangeEcueil(rs.echangeEcueil());
        }
    }
}
