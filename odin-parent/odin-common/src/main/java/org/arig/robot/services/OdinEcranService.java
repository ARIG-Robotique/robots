package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.model.EurobotStatus;
import org.arig.robot.model.ecran.EcranParams;
import org.arig.robot.model.ecran.EcranState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

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
            stateInfos.setOptions(Map.of(
                    EurobotConfig.REVERSE_CARRE_FOUILLE, rs.reverseCarreDeFouille(),
                    EurobotConfig.STOCKAGE_ABRI, rs.stockageAbriChantier()
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
