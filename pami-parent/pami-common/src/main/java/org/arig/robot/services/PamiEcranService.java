package org.arig.robot.services;

import org.arig.robot.constants.ConstantesConfig;
import org.arig.robot.model.EurobotStatus;
import org.arig.robot.model.StrategyOption;
import org.arig.robot.model.ecran.EcranParams;
import org.arig.robot.model.ecran.EcranState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PamiEcranService extends EcranService {

  @Autowired
  private EurobotStatus rs;

  @Override
  public void updateStateInfo(EcranState stateInfos) {
    super.updateStateInfo(stateInfos);
    if (stateInfos.isOtherRobot()) {
      stateInfos.setTeam(rs.team());
      stateInfos.setStrategy(rs.strategy());
      stateInfos.setOptions(Map.of(
          StrategyOption.LIMIT_2_ETAGES.description(), rs.limit2Etages()
      ));
    }
  }

  @Override
  protected EcranParams getParams() {
    EcranParams params = super.getParams();
    params.setName("PAMI " + System.getProperty(ConstantesConfig.keyPamiId));
    params.setPrimary(false);
    params.setPami(true);
    return params;
  }
}
