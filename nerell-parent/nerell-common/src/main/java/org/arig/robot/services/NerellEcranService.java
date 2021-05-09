package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.ecran.UpdateStateInfos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NerellEcranService extends AbstractEcranService {

    @Autowired
    private BaliseService baliseService;

    @Override
    public void updateStateInfo(UpdateStateInfos stateInfos) {
        super.updateStateInfo(stateInfos);
        stateInfos.setBalise(baliseService.isConnected());
    }

}
