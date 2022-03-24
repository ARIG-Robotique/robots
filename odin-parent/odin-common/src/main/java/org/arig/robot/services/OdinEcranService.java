package org.arig.robot.services;

import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.model.EurobotStatus;
import org.arig.robot.model.ecran.EcranParams;
import org.arig.robot.model.ecran.EcranState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OdinEcranService extends EcranService {

    @Autowired
    private EurobotStatus rs;

    @Override
    public void updateStateInfo(EcranState stateInfos) {
        super.updateStateInfo(stateInfos);
        if (stateInfos.isOtherRobot()) {
            stateInfos.setTeam(rs.team());
            stateInfos.setStrategy(rs.strategy());
            stateInfos.setOptions(ImmutableMap.of(
                    EurobotConfig.OPTION_1, rs.option1(),
                    EurobotConfig.OPTION_2, rs.option2()
            ));
        }
    }

    @Override
    protected EcranParams getParams() {
        EcranParams params = super.getParams();
        params.setName("Odin");
        params.setPrimary(false);
        return params;
    }
}
