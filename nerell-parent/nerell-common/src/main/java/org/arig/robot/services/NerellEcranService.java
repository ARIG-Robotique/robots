package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.ecran.EcranParams;
import org.arig.robot.model.ecran.EcranState;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NerellEcranService extends EcranService {

  @Override
  public void updateStateInfo(EcranState stateInfos) {
    super.updateStateInfo(stateInfos);
  }

  @Override
  protected EcranParams getParams() {
    EcranParams params = super.getParams();
    params.setName("Nerell");
    params.setPrimary(true);
    params.setPami(false);
    return params;
  }
}
